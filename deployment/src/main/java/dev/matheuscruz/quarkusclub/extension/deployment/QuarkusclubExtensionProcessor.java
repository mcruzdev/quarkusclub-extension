package dev.matheuscruz.quarkusclub.extension.deployment;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import dev.matheuscruz.quarkusclub.extension.runtime.QuarkusClubLogHandlerRecorder;
import dev.matheuscruz.quarkusclub.extension.runtime.SimpleMessager;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.processor.MethodDescriptors;
import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import io.quarkus.gizmo.AnnotationCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

class QuarkusclubExtensionProcessor {

    private static final String FEATURE = "quarkusclub-extension";
    private static final DotName JAX_RS_POST = DotName.createSimple("jakarta.ws.rs.POST");
    private static final DotName JAX_RS_PATH = DotName.createSimple("jakarta.ws.rs.Path");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(value = ExecutionTime.RUNTIME_INIT)
    LogHandlerBuildItem logHandlerBuildItem(QuarkusClubLogHandlerRecorder recorder) {
        return new LogHandlerBuildItem(recorder.logHandler());
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder().addBeanClasses(
                SimpleMessager.class)
                .setUnremovable()
                .build();
    }

    @BuildStep
    AnnotationsTransformerBuildItem transformer() {

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            @Override
            public void transform(TransformationContext transformationContext) {
                if (transformationContext.getAnnotations().stream()
                        .anyMatch(item -> item.getClass().equals(JAX_RS_POST))) {

                    // transform here

                }
            }

        });
    }

    @BuildStep
    void generatePingResource(BuildProducer<GeneratedClassBuildItem> generatedClasses,
            CombinedIndexBuildItem jandex, BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        GeneratedClassGizmoAdaptor gizmoAdapter = new GeneratedClassGizmoAdaptor(generatedClasses, true);
        String className = "dev.matheuscruz.GeneratedPingResource";
        try (ClassCreator classCreator = ClassCreator.builder()
                .className(className)
                .classOutput(gizmoAdapter)
                .build()) {

            classCreator
                    .addAnnotation(Path.class)
                    .addValue("value", "/ping");

            try (MethodCreator alwaysReturnPong = classCreator.getMethodCreator("ping",
                    String.class)) {
                alwaysReturnPong.setModifiers(Opcodes.ACC_PUBLIC);
                alwaysReturnPong.addAnnotation(GET.class);
                alwaysReturnPong.addAnnotation(Produces.class).addValue("value", MediaType.TEXT_PLAIN);
                alwaysReturnPong.returnValue(alwaysReturnPong.load("pong"));
            }
        }
    }

}
