package service.baseSerivice;

import exceptions.InvalidProductException;
import exceptions.UserNotFoundException;

@FunctionalInterface
public interface BaseService<T> {
    void  add(T o) throws UserNotFoundException, InvalidProductException;

}
