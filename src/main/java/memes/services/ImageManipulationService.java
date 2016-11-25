package memes.services;

public interface ImageManipulationService {
    /**
     * Captions an image file
     * @param file
     * @param topText
     * @param bottomText
     * @return the filename of the captioned meme
     */
    void caption(String fileName, String topText, String bottomText);
}
