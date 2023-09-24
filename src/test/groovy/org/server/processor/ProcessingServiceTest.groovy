import org.server.dao.Dao
import org.server.processor.ProcessingService
import spock.lang.Specification
import spock.lang.Shared

import java.util.concurrent.CountDownLatch
import java.util.function.Consumer

import static java.util.Map.of

class ProcessingServiceTest extends Specification {

    @Shared
    Dao daoMock

    def setupSpec() {
        daoMock = Mock(Dao)
    }

    def "should add operation to queue for processing"() {
        given: "a processing service with enough queue space"
        def processors = of(Object.class, new Consumer<Object>() {
            @Override
            void accept(Object o) {
                println("Processing object: " + o)
            }
        });
        def service = new ProcessingService(5, processors, daoMock)

        when: "we add a task for processing"
        service.putOperationForProcessing(new Object())

        then: "it should not throw any exceptions"
        noExceptionThrown()
    }

    def "should throw exception when shutting down and adding operation"() {
        given: "a processing service in shutting down state"
        def processors = of(Object.class, new Consumer<Object>() {
            @Override
            void accept(Object o) {
                println("Processing object: " + o)
            }
        });
        def service = new ProcessingService(5, processors, daoMock)
        service.awaitTermination()

        when: "we add a task for processing"
        service.putOperationForProcessing(new Object())

        then: "it should throw an exception"
        thrown(RuntimeException)
    }

    def "should throw exception when queue is full"() {
        def countDownLatch = new CountDownLatch(1)
        given: "a processing service with a full queue"
        def processors = of(Object.class, new Consumer<Object>() {
            @Override
            void accept(Object o) {
                countDownLatch.await(); // simulating slow processing
            }
        });
        def service = new ProcessingService(1, processors, daoMock)
        service.putOperationForProcessing(new Object())

        when: "we add another few tasks for processing"
        service.putOperationForProcessing(new Object())
        service.putOperationForProcessing(new Object())
        service.putOperationForProcessing(new Object())

        then: "it should throw an exception"
        thrown(RuntimeException)
    }


    def "should store unfinshed tasks in dao"() {
        given: "a processing service with a full queue"
        def countDownLatch = new CountDownLatch(1)
        def waitTilldaoProcessLatch = new CountDownLatch(1)
        def unprocessedEventsCount = 0
        def daoMock = Mock(Dao) {
            storeUnfinishedTasks(_ as Collection) >> {
                unprocessedEventsCount = it.size()
                waitTilldaoProcessLatch.countDown()
            }
        }
        def processors = of(Object.class, new Consumer<Object>() {
            @Override
            void accept(Object o) {
                countDownLatch.await(); // simulating slow processing
            }
        });
        def service = new ProcessingService(5, processors, daoMock)
        service.putOperationForProcessing(new Object())

        when: "we add another few tasks for processing"
        service.putOperationForProcessing(new Object())
        service.putOperationForProcessing(new Object())
        service.putOperationForProcessing(new Object())

        service.awaitTermination()
        countDownLatch.countDown()
        waitTilldaoProcessLatch.await()

        then: "it should save the unfinished tasks in dao"
        unprocessedEventsCount > 0
    }

}
