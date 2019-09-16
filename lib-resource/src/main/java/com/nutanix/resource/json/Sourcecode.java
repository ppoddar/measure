package com.nutanix.resource.json;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Sourcecode {
	ClassName classname;
	List<Import> imports = new ArrayList<>();
	List<Field> fields = new ArrayList<>();
	
	public static final String PACKAGE = "package";
	public static final String IMPORT = "import";
	public static final String PUBLIC = "public";
	public static final String PRIVATE = "private";
	public static final String CLASS  = "class";
	public static final String RETURN  = "return";
	public static final String BLOCK_START  = "{";
	public static final String BLOCK_END    = "}";
	public static final String SPACE     = " ";
	public static final String SEMICOLON = ";";
	public static final String NEWLINE   = "\n";
	
	public String getClassname() {
		return classname.getFullName();
	}

	public void setClassname(String classname) {
		this.classname = new ClassName(classname);
	}
	
	public void addField(String name, Class<?> type) {
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
		return new Line(PACKAGE, 
				classname.getPackageName(), SEMICOLON);
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
			 writer.write(1, f.getGetMethod());
			 writer.write(1, f.getSetMethod());
		 }
		 writer.write(0, new Line(BLOCK_END));

	}

	public static class ClassName {
		final String fullName;
		private static final char DOT = '.';
		
		public ClassName(String pkg, String cls) {
			fullName = pkg + DOT + cls;
		}
		public ClassName(String full) {
			fullName = full;
		}
		public ClassName(Class<?> cls) {
			this(cls.getName());
		}
		
		public String getSimpleName() {
			int idx = fullName.lastIndexOf(DOT);
			return idx == -1 ? fullName : fullName.substring(idx+1);
		}
		public String getFullName() {
			return fullName;
		}
		public String getPackageName() {
			int idx = fullName.lastIndexOf(DOT);
			return idx == -1 ? "" : fullName.substring(0,idx);
		}
		
		public Line getDeclaration() {
			return new Line(PUBLIC, CLASS, getSimpleName(), BLOCK_START);
		}
	}
	
	public static class Import {
		final ClassName cls;
		public Import(String full) {
			cls = new ClassName(full);
		}
		public Import(Class<?> cls) {
			this(cls.getName());
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
		private String capitalize(String s) {
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
