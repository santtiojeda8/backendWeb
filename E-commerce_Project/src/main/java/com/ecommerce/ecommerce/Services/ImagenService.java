package com.ecommerce.ecommerce.Services;



import com.ecommerce.ecommerce.Entities.Imagen;
import com.ecommerce.ecommerce.Repositories.ImagenRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagenService extends BaseService<Imagen,Long> {
    public ImagenService(ImagenRepository imagenRepository){
        super(imagenRepository);
    }

}