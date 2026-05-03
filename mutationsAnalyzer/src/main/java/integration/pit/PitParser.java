package integration.pit;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PitParser {

    public static List<PitMutation> parse(String filePath) {

        List<PitMutation> mutations = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilder builder =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = builder.parse(file);
            NodeList mutationNodes = doc.getElementsByTagName("mutation");
            System.out.println(mutationNodes);

            for (int i = 0; i < mutationNodes.getLength(); i++) {

                Element m = (Element) mutationNodes.item(i);


                String status = m.getAttribute("status");

                // Only keep SURVIVED
                if (!"SURVIVED".equals(status)) continue;

                String className = m.getElementsByTagName("mutatedClass")
                        .item(0).getTextContent();

                String sourceFile = m.getElementsByTagName("sourceFile")
                        .item(0).getTextContent();

                int lineNumber = Integer.parseInt(
                        m.getElementsByTagName("lineNumber")
                                .item(0).getTextContent()
                );

                String mutator = m.getElementsByTagName("mutator")
                        .item(0).getTextContent();

                String description = m.getElementsByTagName("description")
                        .item(0).getTextContent();

                mutations.add(new PitMutation(
                        className,
                        sourceFile,
                        lineNumber,
                        mutator,
                        description,
                        status
                ));
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return mutations;
    }
}