package com.kanku.service.impl;

import com.kanku.model.Customer;
import com.kanku.model.ProductOrder;
import com.kanku.model.Size;
import com.kanku.model.dto.ProductOrderDto;
import com.kanku.repository.ICustomerRepository;
import com.kanku.repository.IOrderRepository;
import com.kanku.repository.IProductRepository;
import com.kanku.repository.ISizeRepository;
import com.kanku.service.ICartService;
import com.kanku.service.IMailService;
import com.kanku.service.IOrderService;
import com.kanku.service.ISizeService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private ISizeRepository sizeRepository;


    @Autowired
    private ISizeService sizeService;

    @Autowired
    private ICartService cartService;

    @Autowired
    private IMailService mailService;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public ProductOrderDto orderProduct(ProductOrderDto productOrderDto) {

        productOrderDto.getProductOrders().forEach(productOrder -> {

            Size size =sizeRepository.findById(productOrder.getSize().getSizeId()).get();

            if(productOrder.getOrderQuantity()>size.getTotalProductQuantity()){
                return;
            }
            ProductOrder po=new ProductOrder();

            po.setCustomer(productOrderDto.getCustomer());
            po.setOrderDate(LocalDate.now());

            po.setProduct(productOrder.getProduct());
            po.setOrderQuantity(productOrder.getOrderQuantity());
            po.setSize(productOrder.getSize());

            po.setTotalOrderAmount(size.getProductPrice()*productOrder.getOrderQuantity());
            po.setDeliveryStatus(false);

            sizeService.updateProductDetails(po);
            orderRepository.save(po);

        });

        cartService.deleteCartsByCustomer(productOrderDto.getCustomer().getCustomerId());

        List<String> productsList=new ArrayList<>();

        productOrderDto.getProductOrders().forEach(productOrders -> {
            productsList.add(productRepository.findById(productOrders.getProduct().getProductId()).get().getProductName());
        });

        String products = productsList.stream()
                .collect(Collectors.joining(", "));

        Customer customer=customerRepository.findById(productOrderDto.getCustomer().getCustomerId()).get();
        String subject="Your Product Has Been Ordered!";

        String message="Dear "+customer.getFullName()+",\n" +
                "\n" +
                "We're excited to inform you that one of your products, "+products+", has just been ordered on [Your E-commerce Platform Name]! Congratulations on this achievement!\n" +
                "\n" +
                "Order Details:\n" +
                "\n" +
                "Product Name: "+products+"\n" +
                "Customer Name: "+customer.getFullName()+"\n" +
                "Shipping Address: "+customer.getAddress()+"\n" +
                "Our team will start processing the order shortly. You will receive another notification once the order is shipped.\n" +
                "\n" +
                "If you have any questions or need further assistance, please feel free to reach out to us at [Support Email].\n" +
                "\n" +
                "Thank you for being a valued member of our community!\n" +
                "\n" +
                "Best regards,\n" +
                "Kanku collection Team";
        try {
            mailService.sendMail(message,subject,customer.getUsername());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return productOrderDto;
    }

    @Override
    public ProductOrder confirmDelivery(Long orderId) {

        ProductOrder order = orderRepository.findById(orderId).get();

        order.setDeliveryStatus(true);
        return orderRepository.save(order);
    }
}
