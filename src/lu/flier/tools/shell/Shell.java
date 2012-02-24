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

    public Object evaluateScript(String scriptText) throws ScriptException, IOException {
        return this.evaluateScript(scriptText, null);
    }

    public void shutdown() {
        eng = null;
        System.gc();
    }

    public Object evaluateScript(String scriptText, String[] args) throws ScriptException, IOException {
        if (eng == null) {
            eng = new ScriptEngineManager().getEngineByName("jav8");

            Bindings scope = eng.getBindings(ScriptContext.ENGINE_SCOPE);
            scope.put("Jav8Shell", this);

            eng.eval("var exports = {};");
            eng.eval("this.setTimeout = function(f, t) { f(); }");
            eng.eval("this.clearTimeout = function(t) { }");
            eng.eval("this.setInterval = function(f, t) { throw new Error(\"Intervals not supported\"); }");
            eng.eval("this.clearInterval = function(t) { throw new Error(\"Intervals not supported\"); }");
            eng.eval("var arguments = Jav8Shell.getArgs();");
            eng.eval("var environment = null;");
            eng.eval("var history = null;");
            eng.eval("var help = function() { return Jav8Shell.help(); };");
            eng.eval("var defineClass = function() { throw new Error(\"defineClass not implemented.\"); };");
            eng.eval("var deserialize = function() { throw new Error(\"deserialize not implemented.\"); };");
            eng.eval("var gc = function() { return Jav8Shell.gc(); };");
            eng.eval("var load = function(f) { return Jav8Shell.load(f); };");
            eng.eval("var loadClass = function() { throw new Error(\"loadClass not implemented.\"); };");
            eng.eval("var print = function(arr) { return Jav8Shell.print(arr); };");
            eng.eval("var readFile = function(f) { return Jav8Shell.readFile(f); };");
            eng.eval("var readUrl = function(f) { return Jav8Shell.readUrl(f); };");
            eng.eval("var runCommand = function() { throw new Error(\"runCommand not implemented.\"); };");
            eng.eval("var seal = function() { throw new Error(\"seal not implemented.\"); };");
            eng.eval("var serialize = function() { throw new Error(\"serialize not implemented.\"); };");
            eng.eval("var spawn = function() { throw new Error(\"spawn not implemented.\"); };");
            eng.eval("var quit = function() { return Jav8Shell.quit(); };");
            eng.eval("var version = function() { throw new Error(\"version not implemented.\"); };");
            eng.eval("var fileExists = function(f) { return Jav8Shell.fileExists(f); };");

            eng.eval("var console = { log: function() {  var args = []; for (var i = 0; i < arguments.length; i++) " +
                    "{ args[args.length] = arguments[i]; } print(args); } };");
        }

        if (args != null) {
            this.args = args;
        }

        return eng.eval(scriptText);
    }

    public void injectObject(String var, Object object) {
        Bindings scope = eng.getBindings(ScriptContext.ENGINE_SCOPE);
        scope.put(var, object);
    }

    public Object extractObject(String name) {
        Bindings scope = eng.getBindings(ScriptContext.ENGINE_SCOPE);
        return scope.get(name);
    }

    public void invokeMethod(Object target, String name, Object... objects) throws ScriptException, NoSuchMethodException {
        ((Invocable)eng).invokeMethod(target, name, objects);
    }

    public void invokeFunction(String name, Object... objects) throws ScriptException, NoSuchMethodException {
        ((Invocable)eng).invokeFunction(name, objects);
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
