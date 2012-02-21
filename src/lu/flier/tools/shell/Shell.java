package lu.flier.tools.shell;

import lu.flier.script.V8Array;
import lu.flier.script.V8Object;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.script.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;

public class Shell {
    private ScriptEngine eng;
    private String[] args;

    public void evaluateScript(String scriptText, String[] args) throws ScriptException, IOException {
        eng = new ScriptEngineManager().getEngineByName("jav8");
        this.args = args;

        Bindings scope = eng.getBindings(ScriptContext.ENGINE_SCOPE);
        scope.put("Jav8Shell", this);

        eng.eval("var arguments = Jav8Shell.getArgs();");
        eng.eval("var environment = null;");
        eng.eval("var history = null;");
        eng.eval("var help = function() { return Jav8Shell.help(); };");
        eng.eval("var defineClass = function() { throw new Exception(\"defineClass not implemented.\"); };");
        eng.eval("var deserialize = function() { throw new Exception(\"deserialize not implemented.\"); };");
        eng.eval("var gc = function() { return Jav8Shell.gc(); };");
        eng.eval("var load = function(f) { return Jav8Shell.load(f); };");
        eng.eval("var loadClass = function() { throw new Exception(\"loadClass not implemented.\"); };");
        eng.eval("var print = function(arr) { return Jav8Shell.print(arr); };");
        eng.eval("var readFile = function(f) { return Jav8Shell.readFile(f); };");
        eng.eval("var readUrl = function(f) { return Jav8Shell.readUrl(f); };");
        eng.eval("var runCommand = function() { throw new Exception(\"runCommand not implemented.\"); };");
        eng.eval("var seal = function() { throw new Exception(\"seal not implemented.\"); };");
        eng.eval("var serialize = function() { throw new Exception(\"serialize not implemented.\"); };");
        eng.eval("var spawn = function() { throw new Exception(\"spawn not implemented.\"); };");
        eng.eval("var quit = function() { return Jav8Shell.quit(); };");
        eng.eval("var version = function() { throw new Exception(\"version not implemented.\"); };");
        eng.eval("var fileExists = function(f) { return Jav8Shell.fileExists(f); };");

        eng.eval("var console = { log: function() {  var args = []; for (var i = 0; i < arguments.length; i++) " +
                "{ args[args.length] = arguments[i]; } print(args); } };");

        eng.eval(scriptText);
    }

    public boolean fileExists(String path) {
        return (new File(path)).exists();
    }

    public Object evaluateString(V8Object context, String script, String sourceName, int lineno, Object securityDomain) throws ScriptException {
        Compilable compiler = (Compilable) this.eng;
        return compiler.compile(script).eval(context.getContext());
    }

    public void print(V8Array objs) {
        System.out.println(StringUtils.join(objs, " "));
    }

    public String[] getArgs() {
        return args;
    }

    public String readFile(String file) throws IOException {
        return FileUtils.readFileToString(new File(file));
    }

    public void help() {
        System.out.println("Jav8 Shell Help");
    }

    public void gc() {
        System.gc();
    }

    public String readUrl(String urlText) throws IOException {
        URL url = new URL(urlText);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder bld = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            bld.append(line);
        }

        return bld.toString();
    }

    public void load(String fileName) throws IOException, ScriptException {
        this.eng.eval(FileUtils.readFileToString(new File(fileName.toString())));
    }

    public void quit() {
        System.exit(0);
    }

    public String getProperty(String name) {
        return java.lang.System.getProperty(name);
    }

    public File getFile(String path) {
        return new File(path);
    }

    public FileInputStream getFileInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    public FileOutputStream getFileOutputStream(String path) throws FileNotFoundException {
        return new FileOutputStream(path);
    }

    public BufferedReader getBufferedReader(File fileObj, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileObj), encoding));
    }

    public StringBuffer getStringBuffer() {
        return new StringBuffer();
    }

    public OutputStreamWriter getOutputStreamWriter(File outFile, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        return new OutputStreamWriter(new FileOutputStream(outFile), encoding);
    }

    public OutputStreamWriter getOutputStreamWriter(File outFile) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(outFile));
    }

    public BufferedWriter getBufferedWriter(OutputStreamWriter writer) {
        return new BufferedWriter(writer);
    }

    public void printBindings() {
        System.out.println(Arrays.toString(eng.getBindings(ScriptContext.ENGINE_SCOPE).keySet().toArray()));
    }
}
