/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wizut.tpsi.ogloszenia.services;

import java.util.List;
import org.springframework.stereotype.Service;
import wizut.tpsi.ogloszenia.jpa.CarManufacturer;
import wizut.tpsi.ogloszenia.jpa.CarModel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import wizut.tpsi.ogloszenia.jpa.BodyStyle;
import wizut.tpsi.ogloszenia.jpa.FuelType;
import wizut.tpsi.ogloszenia.jpa.Offer;
import wizut.tpsi.ogloszenia.jpa.User;
import wizut.tpsi.ogloszenia.web.OfferFilter;

/**
 *
 * @author pawel
 */
@SuppressWarnings("JpaQlInspection")
@Service
@Transactional
public class OffersService {

    @PersistenceContext
    private EntityManager em;

    public CarManufacturer getCarManufacturer(int id) {
        return em.find(CarManufacturer.class, id);
    }

    public List<CarManufacturer> getCarManufacturers() {
        String jpql = "select cm from CarManufacturer cm order by cm.name";
        TypedQuery<CarManufacturer> query = em.createQuery(jpql, CarManufacturer.class);
        List<CarManufacturer> result = query.getResultList();
        //lub
        //return em.createQuery("select cm from CarManufacturer cm order by cm.name", CarManufacturer.class).getResultList();
        return result;
    }

    public CarModel getModel(int id) {
        return em.find(CarModel.class, id);
    }

    public List<CarModel> getCarModels() {
        String jpql = "select cm from CarModel cm order by cm.name";
        TypedQuery<CarModel> query = em.createQuery(jpql, CarModel.class);
        List<CarModel> result = query.getResultList();
        return result;
    }

    public List<CarModel> getCarModels(int manufacturerId) {
        String jpql = "select cm from CarModel cm where cm.manufacturer.id = :id order by cm.name";
        TypedQuery<CarModel> query = em.createQuery(jpql, CarModel.class);
        query.setParameter("id", manufacturerId);
        return query.getResultList();
    }

    public List<BodyStyle> getBodyStyles() {
        String jpql = "select bs from BodyStyle bs order by bs.name";
        TypedQuery<BodyStyle> query = em.createQuery(jpql, BodyStyle.class);
        List<BodyStyle> result = query.getResultList();
        return result;
    }

    public List<FuelType> getFuelTypes() {
        String jpql = "select ft from FuelType ft order by ft.name";
        TypedQuery<FuelType> query = em.createQuery(jpql, FuelType.class);
        List<FuelType> result = query.getResultList();
        return result;
    }

    public Offer getOffer(int id) {
        return em.find(Offer.class, id);
    }

    public Offer createOffer(Offer offer) {
        em.persist(offer);
        return offer;
    }

    public List<Offer> getOffers() {
        String jpql = "select o from Offer o order by o.title";
        TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);
        List<Offer> result = query.getResultList();
        return result;
    }

    public List<Offer> getOffersByModel(int modelId) {
        String jpql = "select o from Offer o where o.carModel.id = :id order by o.title";
        TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);
        query.setParameter("id", modelId);
        return query.getResultList();
    }

    public List<Offer> getOffersByManufacturer(int manuacturerId) {
        String jpql = "select o from Offer o where o.carModel.manufacturer.id = :id order by o.title";
        TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);
        query.setParameter("id", manuacturerId);
        return query.getResultList();
    }

    public List<Offer> getOffersByManufacturerAndModel(int manuacturerId, int modelId) {
        String jpql = "select o from Offer o where o.carModel.manufacturer.id = :manuacturerId AND o.carModel.id = :modelId order by o.title";
        TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);
        query.setParameter("manuacturerId", manuacturerId);
        query.setParameter("modelId", modelId);
        return query.getResultList();
    }

    public List<Offer> getOffers(OfferFilter offerFilter) {

        boolean wasInIf = false;
        String jpql = "select o from Offer o where ";
        StringBuffer stringBuffer = new StringBuffer(jpql);

        if (offerFilter.getManufacturerId() != null) {
            stringBuffer.append("o.carModel.manufacturer.id = :manuacturerId ");
            wasInIf = true;
        }
        if (offerFilter.getModelId() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.carModel.id = :modelId ");
            } else {
                stringBuffer.append("o.carModel.id = :modelId ");
            }
            wasInIf = true;
        }
        if (offerFilter.getFuelTypeId() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.fuelType.id = :fuelId ");
            } else {
                stringBuffer.append("o.fuelType.id = :fuelId ");
            }
            wasInIf = true;
        }
        if (offerFilter.getYearFrom() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.year >= :yearFrom ");
            } else {
                stringBuffer.append("o.year >= :yearFrom ");
            }
            wasInIf = true;
        }
        if (offerFilter.getYearTo() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.year <= :yearTo ");
            } else {
                stringBuffer.append("o.year <= :yearTo ");
            }
            wasInIf = true;
        }
        if (offerFilter.getPriceFrom() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.price >= :priceFrom ");
            } else {
                stringBuffer.append("o.price >= :priceFrom ");
            }
            wasInIf = true;
        }
        if (offerFilter.getPriceTo() != null) {
            if (wasInIf) {
                stringBuffer.append("AND o.price <= :priceTo ");
            } else {
                stringBuffer.append("o.price <= :priceTo ");
            }
            wasInIf = true;
        }

        if (!wasInIf) {
            stringBuffer.append("1=1 order by o.title");
            jpql = stringBuffer.toString();
            TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);
            return query.getResultList();
        }

        stringBuffer.append("order by o.title");
        jpql = stringBuffer.toString();
        TypedQuery<Offer> query = em.createQuery(jpql, Offer.class);

        if (offerFilter.getManufacturerId() != null) {
            query.setParameter("manuacturerId", offerFilter.getManufacturerId());
        }
        if (offerFilter.getModelId() != null) {
            query.setParameter("modelId", offerFilter.getModelId());
        }
        if (offerFilter.getFuelTypeId() != null) {
            query.setParameter("fuelId", offerFilter.getFuelTypeId());
        }
        if (offerFilter.getYearFrom() != null) {
            query.setParameter("yearFrom", offerFilter.getYearFrom());
        }
        if (offerFilter.getYearTo() != null) {
            query.setParameter("yearTo", offerFilter.getYearTo());
        }
        if (offerFilter.getPriceFrom() != null) {
            query.setParameter("priceFrom", offerFilter.getPriceFrom());
        }
        if (offerFilter.getPriceTo() != null) {
            query.setParameter("priceTo", offerFilter.getPriceTo());
        }

        return query.getResultList();
    }

    public Offer deleteOffer(Integer id) {
        Offer offer = em.find(Offer.class, id);
        em.remove(offer);
        return offer;
    }

    public Offer saveOffer(Offer offer) {
        return em.merge(offer);
    }
    
    public List<User> getUserByUsername(String login) {
    String jpql = "select cm from User cm where cm.username = :login";
    TypedQuery<User> query = em.createQuery(jpql, User.class);
    query.setParameter("login", login);

    return query.getResultList();
    }
    
    public List<User> isValid(String username,String password) {
        
    String jpql = "select cm from User cm where cm.username = :username and cm.password = :password";
    TypedQuery<User> query = em.createQuery(jpql, User.class);
    
    query.setParameter("username", username);
    query.setParameter("password", password);
    List<User> result = query.getResultList();
    return result;
}

}
