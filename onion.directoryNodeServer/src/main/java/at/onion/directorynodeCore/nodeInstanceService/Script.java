package at.onion.directorynodeCore.nodeInstanceService;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by willi on 24.01.15.
 */
public class Script {

    private List<String> lines;

    public Script() {
        lines = new ArrayList<>();
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public String getEncodedScriptAsString() {
        return new String(Base64.encodeBase64(join(lines, "\n").getBytes()));
    }

    private String join(Collection<String> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = s.iterator();
        while(iter.hasNext()) {
            builder.append(iter.next());
            if(!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
