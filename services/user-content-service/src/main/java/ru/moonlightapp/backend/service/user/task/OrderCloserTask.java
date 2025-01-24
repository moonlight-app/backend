package ru.moonlightapp.backend.service.user.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.service.user.api.service.OrdersService;
import ru.moonlightapp.backend.service.user.storage.model.OrderItem;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public final class OrderCloserTask {

    // TODO set relevant threshold somewhen later...
    private static final long ELAPSED_SECONDS_THRESHOLD = 300;

    private final OrdersService ordersService;

    // TODO set relevant rate somewhen later...
    @Scheduled(fixedRate = 30000)
    public void run() {
        List<OrderItem> orderItems = ordersService.closeOrders(ELAPSED_SECONDS_THRESHOLD);
        if (!orderItems.isEmpty()) {
            log.info("Some orders have been closed automatically ({}):", orderItems.size());
            orderItems.forEach(item -> log.info(
                    "- [#{}]: product #{}, ordered at '{}' by user '{}'",
                    item.getId(), item.getProductId(), item.getCreatedAt(), item.getUserEmail())
            );
        }
    }

}
