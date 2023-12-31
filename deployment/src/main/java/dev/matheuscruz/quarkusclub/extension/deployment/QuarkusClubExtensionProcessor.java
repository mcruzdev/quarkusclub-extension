package dev.matheuscruz.quarkusclub.extension.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.objectweb.asm.Opcodes;

import dev.matheuscruz.quarkusclub.extension.runtime.PingResource;
import dev.matheuscruz.quarkusclub.extension.runtime.QuarkusClubLogHandlerRecorder;
import dev.matheuscruz.quarkusclub.extension.runtime.SimpleMessager;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.resteasy.reactive.spi.AdditionalResourceClassBuildItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

class QuarkusClubExtensionProcessor {

    private static final String FEATURE = "quarkusclub-extension";
    private static final DotName JAX_RS_POST = DotName.createSimple("jakarta.ws.rs.POST");
    private static final String GENERATED_PING_RESOURCE_CLASS_NAME = "dev.matheuscruz.GeneratedPingResource";

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
    void generatePingResource(
            BuildProducer<GeneratedClassBuildItem> generatedClasses,
            BuildProducer<AdditionalResourceClassBuildItem> additionalResources,
            CombinedIndexBuildItem jandex) {

        GeneratedClassGizmoAdaptor gizmoAdapter = new GeneratedClassGizmoAdaptor(generatedClasses, true);
        try (ClassCreator classCreator = ClassCreator.builder()
                .className(GENERATED_PING_RESOURCE_CLASS_NAME)
                .classOutput(gizmoAdapter)
                .build()) {

            classCreator
                    .addAnnotation(Path.class)
                    .addValue("value", "/ping");
            classCreator.addAnnotation(ApplicationScoped.class);

            try (MethodCreator alwaysReturnPong = classCreator.getMethodCreator("ping",
                    String.class)) {
                alwaysReturnPong.setModifiers(Opcodes.ACC_PUBLIC);
                alwaysReturnPong.addAnnotation(GET.class);
                alwaysReturnPong.addAnnotation(Produces.class).addValue("value", MediaType.TEXT_PLAIN);
                alwaysReturnPong.returnValue(alwaysReturnPong.load("pong"));
            }
        }
    }

    @BuildStep
    void addPingResource(CombinedIndexBuildItem index,
            BuildProducer<AdditionalResourceClassBuildItem> additionalResourceClassProducer) {

        index.getIndex().getAnnotations(Path.class).stream()
                .forEach(item -> System.out.println(item.target().asClass()));

    }

}
