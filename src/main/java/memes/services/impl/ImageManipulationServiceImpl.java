package memes.services.impl;

import memes.services.ImageManipulationService;
import memes.services.StorageService;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ImageManipulationServiceImpl implements ImageManipulationService {

    private StorageService storageService;
    
    @Autowired
    public ImageManipulationServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public void caption(String fileName, String topText, String bottomText) {
        ConvertCmd cmd = new ConvertCmd();

        IMOperation top = new IMOperation();
        top.addImage(this.storageService.load(fileName).toString());
        top.pointsize(70);
        top.font("Impact");
        top.fill("White");
        top.stroke("Black");
        top.strokewidth(2);
        top.gravity("north");

        top.draw("gravity north text 0,0 '"+ topText + "'");
        top.addImage(this.storageService.load(fileName).toString());

        IMOperation bot = new IMOperation();
        bot.addImage(this.storageService.load(fileName).toString());
        bot.pointsize(70);
        bot.font("Impact");
        bot.fill("White");
        bot.stroke("Black");
        bot.strokewidth(2);
        bot.draw("gravity south text 0,0 '"+ bottomText + "'");
        bot.addImage(this.storageService.load(fileName).toString());

        try {
            cmd.run(top);
            cmd.run(bot);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IM4JavaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
