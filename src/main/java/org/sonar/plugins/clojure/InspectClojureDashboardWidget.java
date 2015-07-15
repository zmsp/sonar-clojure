package org.sonar.plugins.clojure; /**
 * Created by shahadatm on 6/2/15.
 */

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;

@Description("Display metadata of clojure")
@UserRole(UserRole.USER)
public class InspectClojureDashboardWidget extends AbstractRubyTemplate implements RubyRailsWidget {
    String title = "Clojure Widget";
    String id = "org.sonar.plugins.clojure.InspectClojure";

    @Override
    public String getTemplatePath() {
        return "/Files/widget.html.erb";
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
