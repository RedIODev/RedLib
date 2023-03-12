package dev.redio.internal.annotation.concurrent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.TreeMaker;

@SupportedAnnotationTypes("dev.redio.concurrent.Async")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class AsyncProcessor extends AbstractProcessor {

    private int tally;
    private Trees trees;
    private TreeMaker maker;
    private Name.Table names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            var builder = processingEnv.getFiler().createSourceFile("dev.redio.Test");
            try (var pw = new PrintWriter(builder.openWriter())) {
                pw.println("package dev.redio;");
                pw.println("public class Test {}");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
