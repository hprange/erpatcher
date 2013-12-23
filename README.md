ERPatcher
=========
ERPatcher provides tools to patch WebObjects classes using Java instrumentation.

**Version**: 0.1

Requirements
------------
Java 6 or later

Features
--------
Coming soon.

Installation
------------

Maven users have to add the dependency declaration:

	<dependency>
		<groupId>com.erpatcher</groupId>
		<artifactId>erpatcher</artifactId>
		<version>0.1</version>
	</dependency>

Non Maven users have to:

1. Download the erpatcher.jar.
2. Add the ERPatcher library to the build path.

Usage
-----
	import com.erpatcher.ERPatcherAgent;
	
	public class Application extends ERXApplication {
		public static void main(String[] args) {
			ERPatcherAgent.initialize();
			...
		}
	}