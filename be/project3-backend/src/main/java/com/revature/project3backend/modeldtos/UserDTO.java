package com.revature.project3backend.modeldtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revature.project3backend.models.CartItem;
import com.revature.project3backend.models.Transaction;
import com.revature.project3backend.models.User;
import com.revature.project3backend.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO extends User {
    private Integer id;

    /**
     * The first name of the user
     */
    private String firstName;

    /**
     * The last name of the user
     */
    private String lastName;

    /**
     * The email address of the user
     */
    private String email;

    /**
     * The username of the user
     */
    private String username;

    /**
     * The password of the user
     */
    private String password;

    /**
     * The list of cart items in the user's cart
     */
    private List<CartItem> cart;

    /**
     * The list of transactions completed by the user
     */
    private List <Transaction> transactions;

    private UserRole role;

    public UserDTO(User user){
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.cart = user.getCart();
        this.transactions = user.getTransactions();
        this.role = user.getRole();
    }

}
