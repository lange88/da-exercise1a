import interfaces.Message;
import org.junit.Assert;
        import org.junit.Test;

public class DA_BSS_Test {

    @Test
    public void singleProcessMessageFromPast() {
        int[] pastVectorClock = {0};
        int[] futureVectorClock = {1};

        Message messageFromPast = new Message("", pastVectorClock, 0);

        Assert.assertTrue(DA_BSS.isDeliverable(1, messageFromPast, futureVectorClock));
    }

    @Test
    public void singleProcessMessageFromFuture() {
        int[] pastVectorClock = {0};
        int[] futureVectorClock = {1};

        Message messageFromFuture = new Message("", futureVectorClock, 0);

        Assert.assertFalse(DA_BSS.isDeliverable(1, messageFromFuture, pastVectorClock));
    }

    @Test
    public void multiProcessMessageFromPast() {
        int[] pastVectorClock = {1, 1};
        int[] futureVectorClock = {2, 1};

        Message messageFromPast = new Message("", pastVectorClock, 0);

        Assert.assertTrue(DA_BSS.isDeliverable(2, messageFromPast, futureVectorClock));
    }

    @Test
    public void multiProcessMessageFromFuture() {
        int[] pastVectorClock = {1, 1};
        int[] futureVectorClock = {2, 1};

        Message messageFromFuture = new Message("", futureVectorClock, 0);

        Assert.assertFalse(DA_BSS.isDeliverable(2, messageFromFuture, pastVectorClock));
    }

    @Test
    public void bufferStaysEmptyWithDeliverableMessage() throws Exception {
        DA_BSS da_bss = new DA_BSS(0, 1);
        da_bss.vectorClock = new int[]{2};

        int[] messageVectorClock = {1};
        Message message = new Message("", messageVectorClock, 0);

        da_bss.receive(message);
        Assert.assertTrue(da_bss.buffer.isEmpty());
    }

    @Test
    public void bufferAddsUndeliverableMessage() throws Exception {
        DA_BSS da_bss = new DA_BSS(0, 1);
        da_bss.vectorClock = new int[]{1};

        int[] messageVectorClock = {2};
        Message message = new Message("", messageVectorClock, 0);

        da_bss.receive(message);
        Assert.assertFalse(da_bss.buffer.isEmpty());
        Assert.assertEquals(message, da_bss.buffer.poll());
        Assert.assertTrue(da_bss.buffer.isEmpty());
    }
}
