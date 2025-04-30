public class Deleterious {
	public static void abscond() {
		//Commented out because I will totally get flagged for malware.
		//But I'll leave it here for posterity.

		/*

		try {
			//1. get unsafe
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			Unsafe u = (Unsafe) theUnsafeField.get(null);

			//2. find the god lookup
			Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
			Object base = u.staticFieldBase(implLookupField);
			long off = u.staticFieldOffset(implLookupField);
			MethodHandles.Lookup implLookup = (MethodHandles.Lookup) u.getObject(base, off);

			//3. find the URLClassPath on the app classloader
			Class<?> bcpClass = Class.forName("jdk.internal.loader.BuiltinClassLoader");
			Field ucpField = bcpClass.getDeclaredField("ucp");
			VarHandle vh = implLookup.unreflectVarHandle(ucpField);
			Object urlClassPath = vh.get(Extract.class.getClassLoader());

			//4. call "closeLoaders"
			Method closeLoaders = urlClassPath.getClass().getDeclaredMethod("closeLoaders");
			MethodHandle mh = implLookup.unreflect(closeLoaders);
			mh.invoke(urlClassPath);
		} catch (Throwable e) {
			System.err.println("Failed to close the system classpath");
			e.printStackTrace();
		}
		 */
	}
}
