package com.app.tfk.webscraping;

import com.app.tfk.user.ArticleWords;
import com.app.tfk.user.UserInformation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import java.util.concurrent.ExecutionException;

public class Articles{
    private UserInformation userInfo;

    public Articles(UserInformation userInfo) {
        this.userInfo = userInfo;
    }

    public synchronized String[] getAllArticlesElements(UserInformation userInfo, ArticleWords[] articlesInDeck) {
        String[] ret = new String[4]; // title, body, link
        ArticleWords articleWord = userInfo.getArticleAndWordMinusDeckArticleWords(articlesInDeck);
        String url = articleWord.getUrl();
        String word = articleWord.getWord();
        ret[2] = url;
        ret[3] = word;

        try {

            Document document = (new GetAllArticleElements(url, userInfo)).execute().get();

            ret[0] = parseTitle(document);
            ret[1] = parseBody(document).trim();
            ret[2] = document.location();
            userInfo.setArticleWordUrlFromWord(ret[2], word);
            System.out.println("Grabbing document from url: " + ret[2]);
            System.out.println("Adding new card titled: " + ret[0]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String parseTitle(Document document) {
        String title = document.getElementById("firstHeading").text();
        return title;
    }

    private String parseBody(Document document) {
        String[] elementsToBeRemovedById = {
                "siteSub",
                "contentSub2",
                "coordinates",
                "toc",
                "mw-panel",
                "mw-head",
                "catlinks",
                "footer",
                "mw-navigation",
                "firstHeading",
        };
        String[] elementsToBeRemovedByClass = {
                "thumbinner",
                "mw-headline",
                "mw-editsection",
                "infobox",
                "mw-jump-link",
                "hatnote",
                "shortdescription",
                "mw-disambig",
                "reflist",
                "printfooter",
                "navbox",
                "boilerplate",
                "mbox-small",
                "rt-commentedText",
                "mw-redirect"
        };
        String[] elementsToBeRemovedByType = {
                "ul",
                "table",
                "dl",
                "title",
        };
        for (String element: elementsToBeRemovedById) {
            Element tempElement = document.getElementById(element);
            if (tempElement != null) {
                tempElement.remove();
            }
        }
        for (String className: elementsToBeRemovedByClass) {
            for (Element tempElement : document.select("."+className)) {
                if (tempElement != null) {
                    tempElement.remove();
                }
            }
        }
        for (String typeName: elementsToBeRemovedByType) {
            for (Element tempElement : document.select(typeName)) {
                if (tempElement != null) {
                    tempElement.remove();
                }
            }
        }
        document.outputSettings(new Document.OutputSettings().prettyPrint(true));
        document.select("br").append("\n");
        document.select("p").prepend("\n");
        String s = document.html().replaceAll("\\\\n", " ");
        String documentString = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        if (documentString.indexOf("(Redirected from ") != -1) documentString = documentString.substring(documentString.indexOf(")") + 1);
        if (documentString.indexOf(("()")) != -1) documentString = documentString.replace("()", "");
        if (documentString.indexOf(("(, )")) != -1)  documentString = documentString.replace(" (, )", "");

        return ReturnProcessedDocument(documentString); // summarized document
    }

    static {
        System.loadLibrary("native-lib");
    }

    // C++ methods
    public native String ReturnProcessedDocument(String x);
}

