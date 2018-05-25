package ch.frimtec.raspicontrol.application.jawning;

import ch.frimtec.raspicontrol.libraries.fts.Processor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;

@SpringBootApplication
@Import(JAwningConfiguration.class)
public class JAwningApplication {

    private final Processor processor;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JAwningApplication.class, args);
        Processor processor = context.getBean(Processor.class);
        processor.run();
    }

    public JAwningApplication(Processor processor) {
        this.processor = processor;
    }

    @PreDestroy
    public void destroy() {
        this.processor.goDown();
    }
}
