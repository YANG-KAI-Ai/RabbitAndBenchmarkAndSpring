package hello;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import org.openjdk.jmh.annotations.*;



@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@Warmup(iterations = 1)
@Measurement(iterations = 2, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Threads(4)
@State(Scope.Benchmark)
public class ApplicationTest {

    private int customerNum = 20;

    private ConfigurableApplicationContext context;

//    @MockBean
//    private RunnerY runnerY;

//    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
    private Receiver receiver;

    @Setup
    public void init() {
        // Here the Application.class is the spring boot startup class in the project
        context = SpringApplication.run(Application.class);
        // Get the bean to be tested
//        this.runnerY = context.getBean(RunnerY.class);
        this.rabbitTemplate = context.getBean(RabbitTemplate.class);
        this.receiver = context.getBean(Receiver.class);
    }

    @TearDown
    public void down() {
        context.close();
    }


    public static void main(String [] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ApplicationTest.class.getSimpleName())
                .build();
        new Runner(opt).run();

    }


    @Benchmark
    public void first() throws Exception {
        for(int i=0; i < customerNum ; i++){
            rabbitTemplate.convertAndSend(Application.queueName, "Rabbit: Get Milk Tea Order "+i+"!");
            receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        }
    }

    @Benchmark
    public void second() {
        for(int i=0; i < customerNum ; i++){
            MilkTeaStore milkTeaStore = new MilkTeaStore(i);
            Thread milkTeaOrder = new Thread(milkTeaStore);
            milkTeaOrder.setName("order"+customerNum);
            milkTeaOrder.start();
        }
    }




}