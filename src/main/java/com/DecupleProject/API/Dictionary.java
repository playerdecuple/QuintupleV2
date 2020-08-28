package com.DecupleProject.API;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLEncoder;

public class Dictionary {

    public Dictionary() {}

    public String getSearchResultFromWord(String word) throws ParserConfigurationException, SAXException, IOException {

        String wordName = "";
        String wordR = "";

        String q;

        q = URLEncoder.encode(word, "UTF-8");

        String url = "https://stdict.korean.go.kr/api/search.do?key=FA2537653DCF7F7712023783D6106964&type_search=search&q=" + q;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);

        doc.getDocumentElement().normalize();

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        NodeList nList_i = doc.getElementsByTagName("item");

        for (int t = 0; t < nList_i.getLength(); t++) {
            Node nNode = nList_i.item(t);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                wordName = getTagValue("word", eElement);

            }
        }

        NodeList nList_s = doc.getElementsByTagName("sense");

        for (int s = 0; s < nList_s.getLength(); s++) {
            Node nNode = nList_s.item(s);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                wordR = getTagValue("definition", eElement);

            }
        }

        String result = wordR;

        if ("".equals(wordName)) {
            result = "그런 단어는 없습니다.";
        }

        return result;

    }

    private String getTagValue(String tag, Element eElement) {
        NodeList nList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        Node nValue = nList.item(0);

        if (nValue == null) {
            return null;
        }

        return nValue.getNodeValue();
    }

    /* Never used code yet.

    public String getOnlyWordName(String word) throws ParserConfigurationException, IOException, SAXException {
        String wordName = "";

        String q = "";

        q = URLEncoder.encode(word, "UTF-8");

        String url = "https://stdict.korean.go.kr/api/search.do?key=FA2537653DCF7F7712023783D6106964&type_search=search&q=" + q;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);

        doc.getDocumentElement().normalize();

        // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ //

        NodeList nList_i = doc.getElementsByTagName("item");

        for (int t = 0; t < nList_i.getLength(); t++) {
            Node nNode = nList_i.item(t);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                wordName = getTagValue("word", eElement);

            }
        }

        return wordName;
    }

     */


}
