package tr.com.albatu.dal;

import org.springframework.data.repository.CrudRepository;

import tr.com.albatu.entities.Category;

public interface CategoryDal extends CrudRepository<Category, Integer>{

}
