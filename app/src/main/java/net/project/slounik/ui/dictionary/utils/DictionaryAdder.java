package net.project.slounik.ui.dictionary.utils;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import net.project.slounik.ui.dictionary.DictionaryFragment;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DictionaryAdder {
    public static void addDictionary(Uri uri, Context context,int pageIndex,String regex){
        StringBuilder result=new StringBuilder();
        String fileName= DocumentFile.fromSingleUri(context,uri).getName();
        if(fileName.endsWith(".txt")){
            result.append(regex).append("\n");
            try {
                BufferedReader reader=new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
                do {
                    result.append(reader.readLine());
                } while (reader.ready());
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(fileName.endsWith(".pdf")){
            try {
                PDFBoxResourceLoader.init(context);
                PDDocument document=PDDocument.load(context.getContentResolver().openInputStream(uri));
                PDFTextStripper reader=new PDFTextStripper();
                reader.setStartPage(pageIndex);
                result.append(regex).append("\n");
                result.append(reader.getText(document));
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*else if(fileName.endsWith(".docx")){
            try {
                XWPFDocument document=new XWPFDocument(context.openFileInput(fileName));
                ArrayList<XWPFParagraph> paragraphs= (ArrayList<XWPFParagraph>) document.getParagraphs();
                for(XWPFParagraph paragraph:paragraphs){
                    result.append(paragraph.getText());
                }
                document.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }*/
        try {
            BufferedOutputStream writer=new BufferedOutputStream(context.openFileOutput((DictionaryFragment.dictionariesList.size()+1)+".txt",Context.MODE_PRIVATE));
            writer.write(result.toString().getBytes(StandardCharsets.UTF_8));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
