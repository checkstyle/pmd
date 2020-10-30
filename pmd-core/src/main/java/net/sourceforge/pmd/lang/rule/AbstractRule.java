/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Basic abstract implementation of all parser-independent methods of the Rule
 * interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class AbstractRule extends AbstractPropertySource implements Rule {

    private Language language;
    private LanguageVersion minimumLanguageVersion;
    private LanguageVersion maximumLanguageVersion;
    private boolean deprecated;
    private String name = getClass().getName();
    private String since;
    private String ruleClass = getClass().getName();
    private String ruleSetName;
    private String message;
    private String description;
    private List<String> examples = new ArrayList<>();
    private String externalInfoUrl;
    private RulePriority priority = RulePriority.LOW;
    private Set<String> ruleChainVisits = new LinkedHashSet<>();
    private Set<Class<? extends Node>> classRuleChainVisits = new LinkedHashSet<>();
    private RuleTargetSelector myStrategy;

    public AbstractRule() {
        definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
        definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
    }

    @Override
    protected String getPropertySourceType() {
        return "rule";
    }

    /**
     * @deprecated Use {@link #deepCopy()} to create verbatim copies of rules.
     */
    @Deprecated
    public void deepCopyValuesTo(AbstractRule otherRule) {
        otherRule.language = language;
        otherRule.minimumLanguageVersion = minimumLanguageVersion;
        otherRule.maximumLanguageVersion = maximumLanguageVersion;
        otherRule.deprecated = deprecated;
        otherRule.name = name;
        otherRule.since = since;
        otherRule.ruleClass = ruleClass;
        otherRule.ruleSetName = ruleSetName;
        otherRule.message = message;
        otherRule.description = description;
        otherRule.examples = copyExamples();
        otherRule.externalInfoUrl = externalInfoUrl;
        otherRule.priority = priority;
        otherRule.propertyDescriptors = new ArrayList<>(getPropertyDescriptors());
        otherRule.propertyValuesByDescriptor = copyPropertyValues();
        otherRule.ruleChainVisits = new LinkedHashSet<>(ruleChainVisits);
        otherRule.classRuleChainVisits = new LinkedHashSet<>(classRuleChainVisits);
    }

    private List<String> copyExamples() {
        return new ArrayList<>(examples);
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        if (this.language != null && !this.language.equals(language)) {
            throw new UnsupportedOperationException("The Language for Rule class " + this.getClass().getName()
                    + " is immutable and cannot be changed.");
        }
        this.language = language;
    }

    @Override
    public LanguageVersion getMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        if (minimumLanguageVersion != null && !minimumLanguageVersion.getLanguage().equals(getLanguage())) {
            throw new IllegalArgumentException("Version " + minimumLanguageVersion + " does not belong to language " + getLanguage());
        }
        this.minimumLanguageVersion = minimumLanguageVersion;
    }

    @Override
    public LanguageVersion getMaximumLanguageVersion() {
        return maximumLanguageVersion;
    }

    @Override
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
        if (maximumLanguageVersion != null && !maximumLanguageVersion.getLanguage().equals(getLanguage())) {
            throw new IllegalArgumentException("Version " + maximumLanguageVersion + " does not belong to language " + getLanguage());
        }
        this.maximumLanguageVersion = maximumLanguageVersion;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSince() {
        return since;
    }

    @Override
    public void setSince(String since) {
        this.since = since;
    }

    @Override
    public String getRuleClass() {
        return ruleClass;
    }

    @Override
    public void setRuleClass(String ruleClass) {
        this.ruleClass = ruleClass;
    }

    @Override
    public String getRuleSetName() {
        return ruleSetName;
    }

    @Override
    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getExamples() {
        // TODO Needs to be externally immutable
        return examples;
    }

    @Override
    public void addExample(String example) {
        examples.add(example);
    }

    @Override
    public String getExternalInfoUrl() {
        return externalInfoUrl;
    }

    @Override
    public void setExternalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl = externalInfoUrl;
    }

    @Override
    public RulePriority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(RulePriority priority) {
        this.priority = priority;
    }

    /**
     * This implementation returns a new instance of {@link ParserOptions} using
     * default settings.
     *
     * @see Rule#setPriority(RulePriority)
     */
    @Override
    @Deprecated
    public ParserOptions getParserOptions() {
        return new ParserOptions();
    }


    private Set<Class<? extends Node>> getClassRuleChainVisits() {
        if (classRuleChainVisits.isEmpty() && ruleChainVisits.isEmpty()) {
            return Collections.singleton(RootNode.class);
        }
        return classRuleChainVisits;
    }


    /**
     * @deprecated Override {@link #buildTargetSelector()}, this is
     *     provided for legacy compatibility
     */
    @Deprecated
    protected void addRuleChainVisit(Class<? extends Node> nodeClass) {
        classRuleChainVisits.add(nodeClass);
    }

    @Override
    public final RuleTargetSelector getTargetSelector() {
        if (myStrategy == null) {
            myStrategy = buildTargetSelector();
        }
        return myStrategy;
    }

    /**
     * Create the targeting strategy for this rule. Please override
     * this instead of using {@link #addRuleChainVisit(Class)}.
     * Use the factory methods of {@link RuleTargetSelector}.
     */
    @NonNull
    protected RuleTargetSelector buildTargetSelector() {
        Set<Class<? extends Node>> crvs = getClassRuleChainVisits();
        return crvs.isEmpty() ? RuleTargetSelector.forRootOnly()
                              : RuleTargetSelector.forTypes(crvs);
    }

    @Override
    public void start(RuleContext ctx) {
        // Override as needed
    }

    @Override
    public void end(RuleContext ctx) {
        // Override as needed
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolation(Object data, Node node) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, this.getMessage(), new Object[0]);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolation(Object data, Node node, String arg) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, this.getMessage(), new Object[]{arg});
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolation(Object data, Node node, Object[] args) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, this.getMessage(), args);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolationWithMessage(Object data, Node node, String message) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, message, new Object[0]);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolationWithMessage(Object data, Node node, String message, int beginLine, int endLine) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, message, beginLine, endLine, new Object[0]);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String,
     * Object[])
     */
    public void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
        RuleContext ruleContext = (RuleContext) data;
        ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(ruleContext,
                this, node, message, args);
    }

    /**
     * Rules are equal if:
     * <ol>
     * <li>They have the same implementation class.</li>
     * <li>They have the same name.</li>
     * <li>They have the same priority.</li>
     * <li>They share the same properties.</li>
     * </ol>
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false; // trivial
        }

        if (this == o) {
            return true; // trivial
        }

        boolean equality = getClass() == o.getClass();

        if (equality) {
            Rule that = (Rule) o;
            equality = getName().equals(that.getName()) && getPriority().equals(that.getPriority())
                    && getPropertiesByPropertyDescriptor().equals(that.getPropertiesByPropertyDescriptor());
        }

        return equality;
    }

    @Override
    public int hashCode() {
        Object propertyValues = getPropertiesByPropertyDescriptor();
        return getClass().getName().hashCode() + (getName() != null ? getName().hashCode() : 0)
                + getPriority().hashCode() + (propertyValues != null ? propertyValues.hashCode() : 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rule deepCopy() {
        Rule rule = null;
        try {
            rule = getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) {
            // Can't happen... we already have an instance
            throw new RuntimeException(ignored); // in case it happens anyway, something is really wrong...
        }
        rule.setName(getName());
        rule.setLanguage(getLanguage());
        rule.setMinimumLanguageVersion(getMinimumLanguageVersion());
        rule.setMaximumLanguageVersion(getMaximumLanguageVersion());
        rule.setSince(getSince());
        rule.setMessage(getMessage());
        rule.setRuleSetName(getRuleSetName());
        rule.setExternalInfoUrl(getExternalInfoUrl());
        rule.setDescription(getDescription());
        for (final String example : getExamples()) {
            rule.addExample(example);
        }
        rule.setPriority(getPriority());
        for (final PropertyDescriptor<?> prop : getPropertyDescriptors()) {
            // define the descriptor only if it doesn't yet exist
            if (rule.getPropertyDescriptor(prop.name()) == null) {
                rule.definePropertyDescriptor(prop); // Property descriptors are immutable, and can be freely shared
            }

            if (isPropertyOverridden(prop)) {
                rule.setProperty((PropertyDescriptor<Object>) prop, getProperty((PropertyDescriptor<Object>) prop));
            }
        }
        return rule;
    }
}
