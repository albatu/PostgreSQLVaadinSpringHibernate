package tr.com.albatu.dal;

import org.springframework.data.repository.CrudRepository;

import tr.com.albatu.entities.User;

public interface UserDal extends CrudRepository<User, Integer>{

	
}
