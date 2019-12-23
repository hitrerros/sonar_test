package org.sonar.samples.java;

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.java.model.expression.MemberSelectExpressionTreeImpl;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.java.resolve.JavaSymbol;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

import com.google.common.collect.ImmutableList;

/**
 * @author Anton Khitrov
 * @since 18 дек. 2019 г.
 */
@Rule(key = "UnsafeApiInvokeDetected")
public class DetectUnsafeApiRule extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {

        if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTreeImpl methodInvocation = (MethodInvocationTreeImpl) tree;

            Iterable<Tree> children = methodInvocation.children();
            for (Tree t : children) {

                if (!t.is(Tree.Kind.MEMBER_SELECT)) continue;

                IdentifierTree identifierTree = ((MemberSelectExpressionTree) t).identifier();
                if ("defineClass".equals(identifierTree.name())) {

                    Tree parentNode = identifierTree.parent();
                    Iterable<Tree> parentChildren = ((MemberSelectExpressionTreeImpl) parentNode).children();

                    for (Tree par : parentChildren) {

                        if (!par.is(Tree.Kind.METHOD_INVOCATION)) {
                            continue;
                        }
                        Symbol symbol = ((MethodInvocationTree) par).symbol();
                        String sq = ((JavaSymbol.MethodJavaSymbol) symbol).toString();

                        if ("Unsafe#getUnsafe()".equals(sq)) {
                            reportIssue(identifierTree, "Substitute to java.lang.invoke.MethodHandles.Lookup.defineClass");
                        }
                    }
                }
            }
        }
    }
}
