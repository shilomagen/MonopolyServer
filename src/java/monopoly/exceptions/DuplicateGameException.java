/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.exceptions;

/**
 *
 * @author ShiloMangam
 */
public class DuplicateGameException extends RuntimeException{
    public DuplicateGameException(){
         super("Duplicated Game Name");
    }
   
}
