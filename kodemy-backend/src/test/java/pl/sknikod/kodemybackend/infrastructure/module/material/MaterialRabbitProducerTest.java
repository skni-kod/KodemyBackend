package pl.sknikod.kodemybackend.infrastructure.module.material;

public class MaterialRabbitProducerTest extends MaterialRabbitProducer {
    public MaterialRabbitProducerTest() {
        super(null);
    }

    @Override
        public void sendNewMaterialToIndex(Message message) {
            // placeholder
        }

        @Override
        public void sendUpdatedMaterialToReindex(Message message) {
            // placeholder
        }
    }