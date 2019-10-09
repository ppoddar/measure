package com.nutanix.resource.json;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * source code for a Java class with different
 * parts.
 * 
 * @author pinaki.poddar
 *
 */
public class Sourcecode {
	private ClassName   classname;
	private List<Import> imports = new ArrayList<>();
	private List<Field> fields = new ArrayList<>();
	
	public static final String PACKAGE = "package";
	public static final String IMPORT  = "import";
	public static final String PUBLIC  = "public";
	public static final String PRIVATE = "private";
	public static final String CLASS   = "class";
	public static final String RETURN  = "return";
	public static final String BLOCK_START  = "{";
	public static final String BLOCK_END    = "}";
	public static final String SPACE        = " ";
	public static final String SEMICOLON    = ";";
	public static final String NEWLINE      = "\n";
	public static final String DOT          = ".";
	

	/**
	 * gets fully qualified class name.
	 * 
	 * @return
	 */
	public String getClassname() {
		return classname.getFullName();
	}

	public void setClassname(String pkg, String classname) {
		this.classname = new ClassName(pkg, classname);
	}
	public void setClassname(String fullname) {
		this.classname = new ClassName(fullname);
	}
	
	public void addField(String name, Class<?> type) {
		fields.add(new Field(name, type));
	}
	public void addField(String name, String type) {
		fields.add(new Field(name, type));
	}
	
	public StringBuffer writeBlock(String content) {
		return new StringBuffer()
				.append(BLOCK_START)
				.append(content)
				.append(BLOCK_END);
	}
	
	public Line writeClassDeclaration() {
		return new Line(PUBLIC, CLASS, classname.getSimpleName(), BLOCK_START);
	}
	
	public StringBuffer writeImports() {
		StringBuffer buf = new StringBuffer();
		for (Import i : imports) {
			buf.append(writeImport(i));
		}
		return buf;
	}
	
	public StringBuffer writeImport(Import imp) {
		return new StringBuffer()
			.append(IMPORT)
			.append(imp.getClassname().getFullName())
			.append(SPACE).append(SEMICOLON);
	}

	
	public Line writePackage() {
		return classname.getPackage().getDeclaration();
	}
	
	public List<Line> writeFieldDeclarations() {
		List<Line> lines = new ArrayList<>();
		for (Field f : fields) {
			lines.add(writeFieldDeclaration(f));
		}
		return lines;
	}
	
	public Line writeFieldDeclaration(Field f) {
		return new Line(PRIVATE, f.getType().getSimpleName(), f.getName());
	}
	
	public List<Line> writeFieldMethods() {
		List<Line> lines = new ArrayList<>();
		for (Field f : fields) {
			lines.addAll(f.getGetMethod());
			lines.addAll(f.getSetMethod());
			
		}
		return lines;

	}
	
	public void write(PrintStream out) {
		LineWriter writer = new LineWriter(out);
		writer.write(0, writePackage());
		for (Import imp:imports) {
			 writer.write(0, imp.getDeclaration());
		 }
		 writer.write(0, classname.getDeclaration());
		 for (Field f : fields) {
			 writer.write(1, f.getDeclaration());
		 }
		 for (Field f : fields) {
			 writer.write(1, f.getGetMethod());
			 writer.write(1, f.getSetMethod());
		 }
		 writer.write(0, new Line(BLOCK_END));

	}
	
	public static class PackageName {
		final String fullName;
		
		public PackageName(String full) {
			fullName = full;
		}
		
		public PackageName(Class<?> cls) {
			this(cls.getName());
		}
		
		public String getName() {
			return fullName;
		}
				
		public Line getDeclaration() {
			return new Line(PACKAGE, getName(), SEMICOLON);
		}
	}


	public static class ClassName {
		final String name;
		final PackageName pkg;
		
		public ClassName(String pkg, String cls) {
			name = cls;
			this.pkg = new PackageName(pkg);
		}
		
		public ClassName(String fullname) {
			int idx = fullname.lastIndexOf(DOT);
			name = idx == -1 ? fullname : fullname.substring(idx+1);
			this.pkg = new PackageName(idx == -1 ? "" : fullname.substring(0, idx));
		}

		
		public ClassName(Class<?> cls) {
			this(cls.getPackage().getName(), cls.getSimpleName());
		}
		
		public String getSimpleName() {
			return name;
		}
		
		public String getFullName() {
			return pkg.getName() + DOT + name;
		}
		
		public PackageName getPackage() {
			return pkg;
		}
		
		public Line getDeclaration() {
			return new Line(PUBLIC, CLASS, getSimpleName(), BLOCK_START);
		}
	}
	
	public static class Import {
		final ClassName cls;
		public Import(ClassName cls) {
			this.cls = cls;
		}
		public Import(Class<?> cls) {
			this(new ClassName(cls));
		}
		
		public ClassName getClassname() {
			return cls;
		}
		
		public Line getDeclaration() {
			return new Line(IMPORT, getClassname().getFullName());
		}
	}
	
	public static class Field {
		final String name;
		final ClassName type;
		
		public Field(String name, String type ) {
			this.name = name;
			this.type  = new ClassName(type);
		}
		public Field(String name, Class<?> type ) {
			this.name = name;
			this.type  = new ClassName(type);
		}
		
		public ClassName getType() {
			return type;
		}
		
		public String getName() {
			return name;
		}
		
		public List<Line> getGetMethod() {
			List<Line> lines = new ArrayList<>();
			String methodName = "get" + capitalize(getName());
			lines.add(new Line(type.getSimpleName(), 
					methodName, "()", SEMICOLON));
			lines.add(new Line(Sourcecode.RETURN, 
					getName(), Sourcecode.SEMICOLON));
			lines.add(new Line(Sourcecode.BLOCK_END));
			
			return lines;
		}
		
		public List<Line> getSetMethod() {
			List<Line> lines = new ArrayList<>();
			String methodName = "set" + capitalize(getName());
			lines.add(new Line(type.getSimpleName(), 
					methodName, "(", 
					getType().getSimpleName(), getName(), ") {" ));
			lines.add(new Line("this."+getName(), "=", 
					getName(), Sourcecode.SEMICOLON));
			lines.add(new Line(Sourcecode.BLOCK_END));
			
			return lines;
		}

		public Line getDeclaration() {
			return new Line(PUBLIC, 
					getType().getSimpleName(), 
					getName(),
					SEMICOLON);
		}
		
		public static String capitalize(String s) {
			return Character.toUpperCase(s.charAt(0))
			+ s.substring(1);
		}
		
	}
	
	public static class Line {
		private static final String TAB = "   ";
		private static final String SPACE = " ";
		int indentLevel;
		List<String> tokens = new ArrayList<String>();
		
		public Line(String...tokens) {
			this.tokens = Arrays.asList(tokens);
		}
		
		public Line withIndent(int n) {
			indentLevel = n;
			return this;
		}
		public void write(PrintStream out) {
			indent(out, indentLevel);
			for (String t : tokens) {
				out.print(t);
				out.print(SPACE);
			}
			out.println();
		}
		
		void indent(PrintStream out, int n) {
			for (int i = 0; i < n; i++) {
				out.print(TAB);
			}
		}
		
	}



}
