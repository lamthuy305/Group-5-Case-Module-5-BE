package com.example.casemodule6.controller;

import com.example.casemodule6.model.entity.*;
import com.example.casemodule6.service.house.IHouseService;
import com.example.casemodule6.service.notificationdetail.INotificationDetailService;
import com.example.casemodule6.service.order.IOrderService;
import com.example.casemodule6.service.profile.IProfileService;
import com.example.casemodule6.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private INotificationDetailService notificationDetailService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserService userService;

    @Autowired
    IProfileService profileService;


    @GetMapping("/processing/{currentUserId}")
    public ResponseEntity<Iterable<Order>> findAllOrderProcessingByUserId(@PathVariable Long currentUserId) {
        Iterable<Order> orders = orderService.findAllOrderProcessingByUserId(currentUserId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/changeStatusDone/{id}")
    public ResponseEntity<Order> changeStatusOrderDone(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderOptional.get().setStatusOrder(new StatusOrder(2L));
        Long current_count_rent = orderOptional.get().getHouse().getCount_rent();
        orderOptional.get().getHouse().setCount_rent(current_count_rent + 1);
        houseService.save(orderOptional.get().getHouse());
        orderService.save(orderOptional.get());


        Long testCheckin = orderOptional.get().getCheckIn().getTime();
        Long testCheckout = orderOptional.get().getCheckOut().getTime();
        Iterable<Order> ordersProcessing = orderService.findAllOrderProcessingByHouseId(orderOptional.get().getHouse().getId());
        for (Order orderProcessing : ordersProcessing) {
            Long timeCheckin = orderProcessing.getCheckIn().getTime();
            Long timeCheckout = orderProcessing.getCheckOut().getTime();
            if (timeCheckin >= testCheckin && timeCheckin < testCheckout) {
                orderProcessing.setStatusOrder(new StatusOrder(3L));
                orderService.save(orderProcessing);
            } else if (timeCheckout >= testCheckin && timeCheckout < testCheckout) {
                orderProcessing.setStatusOrder(new StatusOrder(3L));
                orderService.save(orderProcessing);
            } else if (timeCheckin <= testCheckin && timeCheckout >= testCheckout) {
                orderProcessing.setStatusOrder(new StatusOrder(3L));
                orderService.save(orderProcessing);
            }
        }
        return new ResponseEntity<>(orderOptional.get(), HttpStatus.OK);
    }

    @GetMapping("/statusDone/{id}")
    public ResponseEntity<Iterable<Order>> findAllOrderStatusDone(@PathVariable Long id) {
        Iterable<Order> orders = orderService.findAllOrderStatusDone(id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @GetMapping("/statusDone/house/{id}")
    public ResponseEntity<Iterable<Order>> getAllOrderStatusDoneByIdHouse(@PathVariable Long id) {
        Iterable<Order> orders = orderService.getAllOrderStatusDoneByIdHouse(id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @GetMapping("/historyOrderTop5/{id}")
    public ResponseEntity<Iterable<Order>> find5OrderByOrderIdRent(@PathVariable Long id) {
        Iterable<Order> orders = orderService.find5OrderByOrderIdRent(id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @GetMapping("/changeStatusCheckin/{id}")
    public ResponseEntity<Order> changeStatusCheckinOrder(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        orderOptional.get().setStatusCheckIn(true);
        orderService.save(orderOptional.get());
        return new ResponseEntity<>(orderOptional.get(), HttpStatus.OK);
    }


    @GetMapping("/changeStatusCanceled/{id}") // Dùng khi admin hủy đơn
    public ResponseEntity<Order> changeStatusOrderCanceled(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderOptional.get().setStatusOrder(new StatusOrder(3L));
        return new ResponseEntity<>(orderService.save(orderOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/customerChangeStatusCanceled/{id}") //Dùng khi khách hàng hủy đơn ( tạo thông báo)
    public ResponseEntity<Order> customerChangeStatusOrderCanceled(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String path = "/admin/history";
        Profile profile = profileService.findByUserId(orderOptional.get().getUser().getId());
        NotificationDetail notificationDetail = new NotificationDetail(new StatusNotification(2L), orderOptional.get().getHouse(), new Date(), path, orderOptional.get().getUser(), profile);
        notificationDetailService.save(notificationDetail);
        orderOptional.get().setStatusOrder(new StatusOrder(3L));
        return new ResponseEntity<>(orderService.save(orderOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/income")
    public ResponseEntity<Iterable<Order>> getHouseInMonthYear(@RequestParam(name = "id") Optional<String> id, @RequestParam(name = "month") Optional<String> month, @RequestParam(name = "year") Optional<String> year) {
        Iterable<Order> orders = orderService.getHouseInMonthYear(id.get(), month.get(), year.get());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderOptional.get(), HttpStatus.OK);
    }

    @GetMapping("/house/{id}")
    public ResponseEntity<Iterable<Order>> getAllOrderByHouseId(@PathVariable Long id) {
        Iterable<Order> orders = orderService.getAllOrderByHouseId(id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order) {
        Optional<House> houseOptional = houseService.findById(order.getHouse().getId());
        order.setStatusOrder(new StatusOrder(1L));
        String path = "/admin/orders";
        Optional<User> userOptional = userService.findById(order.getUser().getId());
        Profile profile = profileService.findByUserId(userOptional.get().getId());
        NotificationDetail notificationDetail = new NotificationDetail(new StatusNotification(1L), houseOptional.get(), new Date(), path, userOptional.get(), profile);
        notificationDetailService.save(notificationDetail);
        return new ResponseEntity<>(orderService.save(order), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        order.setId(id);
        return new ResponseEntity<>(orderService.save(order), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long id) {
        Optional<Order> orderOptional = orderService.findById(id);
        if (!orderOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        orderService.removeById(id);
        return new ResponseEntity<>(orderOptional.get(), HttpStatus.OK);
    }
}
