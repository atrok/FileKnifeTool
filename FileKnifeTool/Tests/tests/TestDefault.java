package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandDelete;
import cmdline.CommandImpl;
import cmdline.CommandParse;
import cmdline.CommandParseFileWithSeparators;
import cmdline.CommandPrint;
import garbagecleaner.ProcessFilesFabric;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.regex.Pattern;

public class TestDefault {

	Path start = Paths.get("Tests/resources/");
	Path resources = Paths.get("target/_temp/resources");
	private static int NUM_OF_FILEPATHS=17;
	private static int NUM_OF_QSP_THREE=3;
	
	@Before
	public void prepareTestEnv() {

		Path target;
		try {
			target = Files.createDirectories(resources);
			walkDirectory(start, new VisitorCopy(target));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@After
	public void cleanTestEnv() {

		walkDirectory(resources, new VisitorDelete());
	}

	@Test
	public void testParse() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			commander.parse("genesys", "-d", resources.toAbsolutePath().toString(), "-ext", ".+config_proxy_person_cto_p_all.20150918_095313_510.log","-sample","10");
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandParse);

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue("Ожидаемое количество найденных файлов - 1, найдено " + i, i == 1);

		} catch (ParameterException ex) {
			ex.printStackTrace();
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSeparator() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			commander.parse("lms", "-d", resources.toAbsolutePath().toString(), "-ext", ".+\\.lms", "-format","csv");
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandParseFileWithSeparators);

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue("Ожидаемое количество найденных файлов - 1, найдено " + i, i == 1);

		} catch (ParameterException ex) {
			ex.printStackTrace();
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSeparatorSQL() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			commander.parse("lms", "-d", resources.toAbsolutePath().toString(), "-ext", ".+\\.lms", "-format","sql");
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandParseFileWithSeparators);

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue("Ожидаемое количество найденных файлов - 1, найдено " + i, i == 1);

		} catch (ParameterException ex) {
			ex.printStackTrace();
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testPrint() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			commander.parse("print", "-d", resources.toAbsolutePath().toString(), "-ext", ".*");
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandPrint);

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue("Ожидаемое количество найденных файлов - "+NUM_OF_FILEPATHS+", найдено " + i, i == NUM_OF_FILEPATHS);

		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Ignore
	public void testPrintOld() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			commander.parse("print", "-d", resources.toAbsolutePath().toString(), "-ext", ".*","-style","old");
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandPrint);

			Map<String, String> results = run((CommandImpl) cmd).getStatData();

			int i = Integer.valueOf(results.get("Found"));
			assertTrue("Ожидаемое количество найденных файлов - "+NUM_OF_FILEPATHS+", найдено " + i, i == NUM_OF_FILEPATHS);

		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Ignore
	public void testDelete() {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		commander.parse("delete", "-d", resources.toAbsolutePath().toString(), "-ext", "(.+\\.qsp)");

//TODO Command is implemented as a singleton, as result you get the same Command object every time you call it
		// because of this all internal structures retains the results of previous Command execution.
		// as of now it's enough to initialize JCommander with comdline arguments one more time to get new Command object
		Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

		
		//((CommandImpl) cmd).resetStatData(); 
		// assertTrue(cmd instanceof CommandDelete);

		Map<String, String> results = run((CommandImpl) cmd).getStatData();

		int i = Integer.valueOf(results.get("Processed"));
		assertTrue("Ожидаемое количество найденных файлов - "+NUM_OF_QSP_THREE+", найдено " + i, i == NUM_OF_QSP_THREE);

	}

	@Ignore
	public void test() { /// walk through the directories and do what's Visitor
							/// is said to do

	}

	@Ignore
	public void test2() { // get the list of files as per matching pattern

		Pattern pattern = Pattern.compile(".*\\.log");
		FileVisitorImpl visitor = new FileVisitorImpl(pattern.toString());
		try {
			Files.walk(start, Integer.MAX_VALUE).map(s -> s.toString()).filter(s -> pattern.matcher(s).matches())
					.forEach(s -> {
						try {
							visitor.visit(Paths.get(s));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		visitor.getStatData();

	}

	private class FileVisitorImpl {

		String pattern = "";
		int counter = 0;

		public FileVisitorImpl(String pattern) {
			this.pattern = pattern;
		}

		public void visit(Path path) throws IOException {
			counter++;
			System.out.println(path);

		}

		public void getStatData() {
			System.out.println("Всего: " + counter);
		}

	}

	public void walkDirectory(Path start, SimpleFileVisitor visitor) {

		try {
			Files.walkFileTree(start, visitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class VisitorCopy extends SimpleFileVisitor<Path> {

		Path target;

		public VisitorCopy(Path target) {
			this.target = target;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.copy(file, target.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e == null) {
				return FileVisitResult.CONTINUE;
			} else {
				// directory iteration failed
				throw e;
			}
		}
	}

	class VisitorDelete extends SimpleFileVisitor<Path> {

		Path target;

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e == null) {
				Files.delete(dir);
				;
				return FileVisitResult.CONTINUE;
			} else {
				// directory iteration failed
				throw e;
			}
		}
	}

	private CommandImpl run(CommandImpl cmd) {
		cmd.getExtensions().forEach(s -> {
			ProcessFilesFabric.create((CommandImpl) cmd, s).start(cmd.getPaths());

		});
		return cmd;
	}

}
