import sun.misc.Unsafe;

class UnsafeCall {

    void foo() {
        sun.misc.Unsafe.getUnsafe().defineClass("clazz", null, 0, 100, new ClassLoader() {
            @Override
            public String getName() {
                return super.getName();
            }
        },new ProtectionDomain(null,null));
    }
}
