/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wizut.tpsi.ogloszenia;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import static wizut.tpsi.ogloszenia.HomeController.disable;
import static wizut.tpsi.ogloszenia.HomeController.filter;
import wizut.tpsi.ogloszenia.jpa.CarManufacturer;
import wizut.tpsi.ogloszenia.jpa.CarModel;
import wizut.tpsi.ogloszenia.jpa.Offer;
import wizut.tpsi.ogloszenia.jpa.User;
import wizut.tpsi.ogloszenia.services.OffersService;
import wizut.tpsi.ogloszenia.web.DescriptionFilter;
import wizut.tpsi.ogloszenia.web.LoginUser;
import wizut.tpsi.ogloszenia.web.OfferFilter;

/**
 *
 * @author wb39480
 */
@Controller
@Scope("session")
public class UsersController {

    @Autowired
    OffersService offersService;

   // static boolean disable = true;

    static int filter = 0;

    
    @RequestMapping("/login")
    public String home(Model model, LoginUser loginUser, HttpServletRequest request) {
        
        String sesssja = (String) request.getSession().getAttribute("sesja");
        
        if(sesssja!=null)
        {
            return "redirect:/";
        }
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);
        
        return "login";
    }

    @GetMapping("/logout")
    public String logOut(Model model, HttpServletRequest request)
            throws ServletException, IOException {

        request.getSession().invalidate();

        return "redirect:/";
    }

    @GetMapping("/valid")
    public String checkValid(Model model, LoginUser loginUser, User user, OfferFilter offerFilter, HttpServletRequest request)
            throws ServletException, IOException {

        List<User> users = offersService.isValid(loginUser.getUsername(), loginUser.getPassword());

        if (users.size() == 1) {
            String login = (String) ((users.get(0)).getUsername());
            
            boolean invalid = true;
            model.addAttribute("inValid", invalid);
            model.addAttribute("user", login);

            String sesssja = (String) request.getSession().getAttribute("sesja");
            if (sesssja == null) {
                request.getSession().setAttribute("sesja", login);
                model.addAttribute("s", login);
            } else {
                request.getSession().setAttribute("sesja", sesssja);
                model.addAttribute("s", sesssja);
            }

            List<CarManufacturer> carManufacturers = offersService.getCarManufacturers();
            List<CarModel> carModels = offersService.getCarModels();

            List<Offer> offers = null;

            boolean newCar = false;

            if (offerFilter.getManufacturerId() != null) {

                if (filter == 0) {
                    filter = offerFilter.getManufacturerId();
                } else {
                    if (filter != offerFilter.getManufacturerId()) {
                        filter = offerFilter.getManufacturerId();
                        newCar = true;
                    }
                }

                if (offerFilter.getModelId() != null && newCar == false) {
                    offers = offersService.getOffersByModel(offerFilter.getModelId());
                    carModels = offersService.getCarModels(offerFilter.getManufacturerId());
                    disable = false;

                } else if (offerFilter.getModelId() != null && newCar == true) {
                    offers = offersService.getOffersByManufacturer(offerFilter.getManufacturerId());
                    carModels = offersService.getCarModels(offerFilter.getManufacturerId());

                } else {
                    offers = offersService.getOffersByManufacturer(offerFilter.getManufacturerId());
                    carModels = offersService.getCarModels(offerFilter.getManufacturerId());
                    disable = false;

                    newCar = false;
                }

            } else {
                offers = offersService.getOffers();
                disable = true;

                newCar = false;
            }

            model.addAttribute("carManufacturers", carManufacturers);
            model.addAttribute("carModels", carModels);
            model.addAttribute("offers", offers);
            model.addAttribute("dis", disable);

            return "redirect:/";
        } else {
            boolean invalid = true;
            model.addAttribute("inValid", invalid);
            model.addAttribute("users", users);
            return "login";
        }
    }

}
