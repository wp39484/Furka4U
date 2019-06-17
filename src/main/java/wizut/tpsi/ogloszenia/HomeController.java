/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wizut.tpsi.ogloszenia;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wizut.tpsi.ogloszenia.jpa.BodyStyle;
import wizut.tpsi.ogloszenia.jpa.CarManufacturer;
import wizut.tpsi.ogloszenia.jpa.CarModel;
import wizut.tpsi.ogloszenia.jpa.FuelType;
import wizut.tpsi.ogloszenia.jpa.Offer;
import wizut.tpsi.ogloszenia.jpa.User;
import wizut.tpsi.ogloszenia.services.OffersService;
import wizut.tpsi.ogloszenia.web.OfferFilter;

/**
 *
 * @author pawel
 */
@Controller
public class HomeController {

    @Autowired
    OffersService offersService;
    static int i = 0;
    static boolean disable = true;
    static int filter = 0;

    @GetMapping("/")
    public String home(Model model, OfferFilter offerFilter, HttpServletRequest request) {
        List<CarManufacturer> carManufacturers = offersService.getCarManufacturers();
        List<CarModel> carModels = offersService.getCarModels();

        List<Offer> offers;
        
        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        if (offerFilter.getManufacturerId() == null && offerFilter.getModelId() == null) {    //pierwsze ladowanie
            offers = offersService.getOffers(offerFilter);
            carModels = null;
        } else if (offerFilter.getManufacturerId() != null && offerFilter.getModelId() == null) {     //wybrano tylko marke
            offers = offersService.getOffers(offerFilter);
            carModels = offersService.getCarModels(offerFilter.getManufacturerId());
        } else if (offerFilter.getManufacturerId() != null && offerFilter.getModelId() != null) {
            offers = offersService.getOffers(offerFilter);
            carModels = offersService.getCarModels(offerFilter.getManufacturerId());
        } else {
            offers = offersService.getOffers(offerFilter);
            carModels = null;
            
            disable = true;
        }

        List<FuelType> fuelTypes = offersService.getFuelTypes();

        List<BodyStyle> bodyStyles = offersService.getBodyStyles();

        model.addAttribute("offerFilter", offerFilter);

        model.addAttribute("fuelTypes", fuelTypes);
        model.addAttribute("carManufacturers", carManufacturers);
        model.addAttribute("carModels", carModels);
        model.addAttribute("offers", offers);

        return "offersList";
    }

    @GetMapping("/offer/{id}")
    public String offerDetails(Model model,
            @PathVariable("id") Integer id, HttpServletRequest request
    ) {
        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);
        Offer offer = offersService.getOffer(id);
        model.addAttribute("offer", offer);
        return "offerDetails";
    }

    @GetMapping("/newoffer")
    public String newOfferForm(Model model, Offer offer,
             HttpServletRequest request
    ) {

        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        List<User> user = offersService.getUserByUsername(sesssja);
        offer.setUser(user.get(0).getId());

        List<CarModel> carModels = offersService.getCarModels();
        List<BodyStyle> bodyStyles = offersService.getBodyStyles();
        List<FuelType> fuelTypes = offersService.getFuelTypes();

        model.addAttribute("carModels", carModels);
        model.addAttribute("bodyStyles", bodyStyles);
        model.addAttribute("fuelTypes", fuelTypes);
        model.addAttribute("header", "Nowe ogłoszenie");
        model.addAttribute("action", "/newoffer");

        return "offerForm";
    }

    @PostMapping("/newoffer")
    public String saveNewOffer(Model model,
            @Valid Offer offer, BindingResult binding,
             HttpServletRequest request
    ) {
        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        List<User> user = offersService.getUserByUsername(sesssja);
        offer.setUser(user.get(0).getId());

        if (binding.hasErrors()) {
            List<CarModel> carModels = offersService.getCarModels();
            List<BodyStyle> bodyStyles = offersService.getBodyStyles();
            List<FuelType> fuelTypes = offersService.getFuelTypes();

            model.addAttribute("carModels", carModels);
            model.addAttribute("bodyStyles", bodyStyles);
            model.addAttribute("fuelTypes", fuelTypes);
            model.addAttribute("header", "Nowe ogłoszenie");
            model.addAttribute("action", "/newoffer");

            return "offerForm";
        }
        offer = offersService.createOffer(offer);

        return "redirect:/offer/" + offer.getId();
    }
    @GetMapping("/deleteoffer/{id}")
    public String deleteOffer(Model model,
            @PathVariable("id") Integer id, HttpServletRequest request
    ) {

        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        List<User> user = offersService.getUserByUsername(sesssja);
        Integer sesja = 0;

        Offer offer = offersService.getOffer(id);

        Integer baza = offer.getUser();
        if (sesssja != null) {
            sesja = user.get(0).getId();
        }

        if (baza == sesja && baza != null) {
            offer = offersService.deleteOffer(id);
            model.addAttribute("offer", offer);
            return "deleteOffer";
        }

        model.addAttribute("offer", offer);
        /////////////////////////////////////

        return "redirect:/offer/" + offer.getId();
    }

    @GetMapping("editoffer/{id}")
    public String editOffer(Model model, @PathVariable("id") Integer id, HttpServletRequest request) {
        
        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        List<User> user = offersService.getUserByUsername(sesssja);
        Integer sesja = 0;
        Offer offer = offersService.getOffer(id);

        Integer baza = offer.getUser();
        if (sesssja != null) {
            sesja = user.get(0).getId();
        }

        if (baza == sesja && baza != null) {

            List<CarModel> carModels = offersService.getCarModels();
            List<BodyStyle> bodyStyles = offersService.getBodyStyles();
            List<FuelType> fuelTypes = offersService.getFuelTypes();

            model.addAttribute("carModels", carModels);
            model.addAttribute("bodyStyles", bodyStyles);
            model.addAttribute("fuelTypes", fuelTypes);

            offer = offersService.getOffer(id);
            model.addAttribute("offer", offer);
            model.addAttribute("header", "Edycja ogłoszenia");
            model.addAttribute("action", "/editoffer/" + id);
            return "offerForm";
        }
        
        
        return "redirect:/offer/" + offer.getId();
    }
    
    @PostMapping("/editoffer/{id}")
    public String saveEditedOffer(Model model,
            @PathVariable("id") Integer id,
            @Valid Offer offer, BindingResult binding,
             HttpServletRequest request
    ) {
        String sesssja = (String) request.getSession().getAttribute("sesja");
        request.getSession().setAttribute("sesja", sesssja);
        model.addAttribute("s", sesssja);

        List<User> user = offersService.getUserByUsername(sesssja);
        offer.setUser(user.get(0).getId());

        if (binding.hasErrors()) {
            model.addAttribute("header", "Edycja ogłoszenia");
            model.addAttribute("action", "/editoffer/" + id);

            List<CarModel> carModels = offersService.getCarModels();
            List<BodyStyle> bodyStyles = offersService.getBodyStyles();
            List<FuelType> fuelTypes = offersService.getFuelTypes();

            model.addAttribute("carModels", carModels);
            model.addAttribute("bodyStyles", bodyStyles);
            model.addAttribute("fuelTypes", fuelTypes);

            return "offerForm";
        }

        offersService.saveOffer(offer);

        return "redirect:/offer/" + offer.getId();
    }
}
