package com.kanku.service.impl;

import com.kanku.model.Cart;
import com.kanku.model.Customer;
import com.kanku.model.Product;
import com.kanku.model.Size;
import com.kanku.repository.ICartRepository;
import com.kanku.repository.ICustomerRepository;
import com.kanku.repository.IProductRepository;
import com.kanku.repository.ISizeRepository;
import com.kanku.service.ICartService;
import com.kanku.service.IMailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {


    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IMailService mailService;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ISizeRepository sizeRepository;

    @Override
    public Cart addToCart(Cart cart) throws MessagingException {
        cart.setCartDate(LocalDate.now());

        Long sizeId= cart.getSizes().get(0).getSizeId();

        Size size =sizeRepository.findById(sizeId).get();

        Product product =productRepository.findById(size.getProduct().getProductId()).get();

        Customer customer=customerRepository.findById(cart.getCustomer_id()).get();

        String message="Dear "+customer.getFullName()+",\n" +
                "\n" +
                "Thank you for shopping with us at Kanku collection! We're excited to let you know that you've added the following item to your cart:\n" +
                "\n" +
                "Product: "+product.getProductName()+"\n" +
                "Size: "+size.getSizeType()+"\n" +
                "Price: "+size.getProductPrice()+"\n" +
                "\n" +
                "Your cart is waiting for you whenever you're ready to checkout. Remember, this item might sell out quickly, so don't wait too long!\n" +
                "\n" +
                "If you need any assistance or have any questions, feel free to contact our customer support.\n" +
                "\n" +
                "Happy shopping!\n" +
                "\n" +
                "Best regards,\n" +
                "Kanku collection\n" +
                "+91 7588267171\n" +
                "[Website URL]";



        String subject="You've Added "+product.getProductName()+" to Your Cart!";
        String status=mailService.sendMail(message,subject,customer.getUsername());
        System.out.println(status);
        return cartRepository.save(cart);
    }

    @Override
    public String deleteCartById(Long cartId) {
        cartRepository.deleteById(cartId);
        if(cartRepository.existsById(cartId)){
            return "product not removed from cart...!";
        }else{
            return "product removed from cart...!";
        }
    }

    @Override
    public List<Cart> getAllCartsByCustomer(Long id) {
        return cartRepository.getAllByCustomerCustomerId(id);
    }

    @Override
    public Cart updateCartQuantity(Cart cart) {

        Cart c=cartRepository.findById(cart.getCartId()).get();

        c.setCartProductQuantity(cart.getCartProductQuantity());
        return cartRepository.save(c);
    }

    @Override
    public String deleteCartsByCustomer(Long customerId) {

        cartRepository.deleteCartsByCustomerId(customerId);
        return "Cart are deleted by customer id...!";
    }


}
