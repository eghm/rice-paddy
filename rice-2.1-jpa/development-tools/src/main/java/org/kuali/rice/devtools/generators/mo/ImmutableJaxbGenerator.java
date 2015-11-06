/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.generators.mo;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This started as simple utility class which generates an "immutable" object complete with a builder based on a supplied
 * contract interface definition.  Several more generators have been added.  Bos, Service interfaces, basic ServiceImpls,
 * AttributeBos, and tests.
 *
 * Using the com.sun.codelmodel means that newly generated classes that depend on each other will not be found.  You'll
 * have to save those yourself and rerun.  It also means groovy can't be generated.
 *
 * The generated classes will have a few compile errors from missing imports, usually a trivial fix via IDE.
 *
 * You can declare fields to be not blank and/or null using JVM args -DNOT_BLANK=typeId,sequenceNumber this adds them to
 * Util.VALIDATED_FIELDS_NOT_BLANK (which already includes id and namespace.
 *
 * Use -DFOREIGN_KEY=key1:org.kuali.rice.project.keyType1,key2:org.kuali.rice.project.keyType2 for fields that are foreign keys.
 * These values will be passed to Class.forName so the fully qualfied classname must be used.
 *
 * Use -DENUM=RelationshipType:UNKNOWN:USAGE_ALLOWED,AnotherType:VALUE1:VALUE2 for the Builder, Enum class generation not
 * implemented yet.
 *
 * -DOUTPUT_FILE to write output to a file, existing file will be overwritten.
 *
 * -DPRE_FETCH=key1,key2 for objects which should conveniently be pre-prefetched when the Bo is populated.
 *
 * If running from Intellij, create a project-gen package including this package and your project-impl (see krms/gen/pom.xml).
 * Additionaly I have found it easier to run the generation from one instance of Intellij and work with the generated files
 * in another.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
// TODO -DPRE_FETCH= for FKs to prefetch
/* prefetch for Contract
	 * Returns the list of agendas {@link AgendaDefinitionContract} contained in the context definition.
	 * This method should never return null. An empty list is returned
     * if no agendas are associated with this context.
	 *
	 * @return the list of agendas contained in this context definition

	 List<? extends AgendaDefinitionContract> getAgendas();

// Def:
	@XmlElementWrapper(name = Elements.AGENDAS)
	@XmlElement(name = Elements.AGENDA, required = false)
	private final List<AgendaDefinition> agendas;

// Def constructor:

    	this.agendas = constructAgendas(builder.getAgendas());

    private static List<AgendaDefinition> constructAgendas(List<AgendaDefinition.Builder> agendaBuilders) {
    	List<AgendaDefinition> agendas = new ArrayList<AgendaDefinition>();
    	if (agendaBuilders != null) {
    		for (AgendaDefinition.Builder agendaBuilder : agendaBuilders) {
    			agendas.add(agendaBuilder.build());
    		}
    	}
    	return agendas;
    }


// in the Builder:
        private List<AgendaDefinition.Builder> agendas;

         * Sets the agendas property of this context definition.
         * <p>For each of the {@link AgendaDefinitionContract} provided in the parameter list,
         * construct an AgendaDefinition from the builder of the provided contract, and save the agenda definitions
         * in a List of {@link AgendaDefinition}</p>
         *
         * @param agendaContracts a list of agenda definition contracts
     public void setAgendas(List<? extends AgendaDefinitionContract> agendaContracts) {
         this.agendas = new ArrayList<AgendaDefinition.Builder>();
         if (agendaContracts != null) for (AgendaDefinitionContract agendaContract : agendaContracts) {
                 this.agendas.add(AgendaDefinition.Builder.create(agendaContract));
         }
     }



 */
// TODO finish/fix Attribute code
// TODO generate the things marked with TODO gen (in TypeTypeRelation, ReferenceObjectBinding, NaturalLanguage Template and Usage)
// TODO .Builder pattern in Builder for objects.
// TODO generate the other finder, the one implemented in the ServiceImpl below
// TODO multi-parameter finds
// TODO javadoc finder methods
// TODO BO name should remove subpackage after .repository
// TODO Object/Id setting see AgendaBo.setRule
// TODO caching annotations
// TODO generate (more thorough) validations.  Validations annotations on Contract get methods and map to sets. Maybe use OVal
// TODO type lookup for OJB generation
// TODO Enum class generation
// TODO QueryResult class generation
// TODO generate AgendaBoServiceTest's style well formed but bad Id and Set methods to construct
// TODO look into generating Remote tests (but can't be in groovy using this generator) RuleRepositoryServiceImplRemoteTest.groovy
public class ImmutableJaxbGenerator {
    public static void main(String[] args) throws Exception {

        if (args.length > 2 || args.length < 1) {
            System.err.println("There should be two arguments defined as follows:\n" +
                    "     1. Fully qualified class name of a 'contract' interface\n" +
                    "     2. [Optional] Fully qualified class name of the class to generate.  If not specified, will use the name of the contract interface class and remove \"Contract\" from the end of it.\n");
            System.exit(1);
        }
        // argument one should be a fully qualified class name of a "contract" interface
        String contractInterfaceName = args[0];
        String className = null;
        // argument two should be the fully qualified class name of the class to generate
        if (args.length == 2) {
            className = args[1];
        } else {
            if (!contractInterfaceName.endsWith("Contract")) {
                throw new IllegalArgumentException("If not explicitly specifying target classname, then contract class name must end with 'Contract'");
            }
            if (Util.IS_KRMS) {
                className = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("Contract"));
            } else {
                if (contractInterfaceName.lastIndexOf("DefinitionContract") > -1) {
                    className = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("DefinitionContract"));
                } else {
                    className = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("Contract"));
                }
            }
        }
        // JVM args
        Util.getNotBlankSystemProperty(); // -DNOT_BLANK=typeId,sequenceNumber gets added to Util.VALIDATED_FIELDS_NOT_BLANK
        Map<String, String> foreignKeyTypes = Util.getForeignKeySystemProperty(); // -DFOREIGN_KEY=key1:keyType1,key2:keyType2
        Map<String, List<String>> enumsValues = Util.getEnumSystemProperties(); // -DENUM=Relationship:UNKNOWN:USAGE_ALLOWED,Another:VALUE1:VALUE2

        // Bo name
        String baseName = null;
        if (contractInterfaceName.endsWith("DefinitionContract")) {
            baseName = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("DefinitionContract"));
        } else {
            baseName = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("Contract"));
        }
        String boClassName = baseName +"Bo";
        boClassName = boClassName.replace(".api.", ".impl.").replace(".language.", ".").replace(".typerelation.", "."); // <-- TODO Bo name remove subpackages after repository

        // Service name, class, and methods.
        String serviceClassName = boClassName + "Service";
        if (baseName.endsWith("Attribute")) {
            serviceClassName = baseName + "Service";
        }

        // flag for attribute code
        boolean hasAttributes = false;
        Class<?> contractInterface = Class.forName(contractInterfaceName);
        List<FieldModel> fields = determineFields(contractInterface);
        if (contains(fields, "attributes")) {
            hasAttributes = true;
        }
        // flag for enum code
        boolean hasEnum = false;
        if (!enumsValues.isEmpty()) {
            System.out.println("Enumeration class generation not yet available, coping an existing and replacing on the type name and value is easy.");
            System.out.println("The Builder will be generated to use the enumeration class.");
            hasEnum = true;
        }

        /*
          #####
         #     # ###### #    # ###### #####    ##   ##### ######
         #       #      ##   # #      #    #  #  #    #   #
         #  #### #####  # #  # #####  #    # #    #   #   #####
         #     # #      #  # # #      #####  ######   #   #
         #     # #      #   ## #      #   #  #    #   #   #
          #####  ###### #    # ###### #    # #    #   #   ######
         */
        // Utility methods are at the bottom of the file.
        // NOTE the generated objects depend on one another.  Generator's saved class file is needed for BoGenerator and so on.
        StringBuilder generatedOutput = new StringBuilder();
        Generator generator = new Generator(contractInterfaceName, className, enumsValues);
        generatedOutput.append(generator.generate()).append("\n");

        String contractTestClass = baseName + "GenTest";
        ContractTestGenerator contractTest = new ContractTestGenerator(contractInterfaceName, contractTestClass, fields, foreignKeyTypes, enumsValues);
        generatedOutput.append(contractTest.generate()).append("\n");

        if (className.endsWith("KrmsTypeRepositoryServiceImpl")) {
            boClassName = boClassName.replace(".type.", "."); // TODO
        } else if (className.endsWith("TypeTypeRelationBoServiceImpl")) {
            boClassName = boClassName.replace(".typerelation.", "."); // TODO
        } else {
            boClassName = boClassName.replace(".reference.", "."); // TODO
        }

        BoGenerator boGenerator = new BoGenerator(contractInterfaceName, boClassName, className, hasAttributes);
        generatedOutput.append(boGenerator.generate()).append("\n");

        QueryResultsGenerator ojbGenerator = new QueryResultsGenerator(contractInterfaceName, boClassName, className, foreignKeyTypes, hasAttributes);
        generatedOutput.append(ojbGenerator.generate()).append("\n");

        // Attribute's don't use Sevice and ServiceImpl classes
        if (boClassName.endsWith("AttributeBo")) {
            if (System.getProperty("OUTPUT_FILE") != null) {
                FileUtils.writeStringToFile(new File(System.getProperty("OUTPUT_FILE")), generatedOutput.toString(), false); // overwrite
            }
            System.exit(1);
        }

        if (serviceClassName.endsWith("KrmsTypeBoService")) {
            serviceClassName = serviceClassName.replace("KrmsTypeBoService","KrmsTypeRepositoryService").replace(".impl.", ".api.");
        } else {
            serviceClassName = serviceClassName.replace(".api.",".impl.").replace(".language", ""); // <-- TODO
            serviceClassName = serviceClassName.replace(".typerelation.", "."); // <-- TODO
            serviceClassName = serviceClassName.replace(".reference.","."); // <-- TODO
        }
        Class<?> serviceInterface = Class.forName(serviceClassName);
        List<Method> methods = Util.determineMethods(serviceInterface);

        ServiceGenerator serviceGenerator = new ServiceGenerator(serviceClassName, className, contractInterfaceName, enumsValues, methods);
        generatedOutput.append(serviceGenerator.generate()).append("\n");


        if (hasAttributes) {
            // Currently generating an AttributeDefinitionContract which you can feed back into the program to produce the Attribute
            // and AttributeBo.  AttributeService and AttributeServiceImpl are not needed for Attributes.
            String attributeContractClassName = boClassName.substring(0, boClassName.lastIndexOf("Bo")) + "AttributeContract";
            AttributeContractGenerator attributeContractGenerator = new AttributeContractGenerator(attributeContractClassName);
            generatedOutput.append(attributeContractGenerator.generate()).append("\n");
        }

        // Contract/Builder test class name
//        String contractTestClass = boClassName.substring(0, boClassName.indexOf("Bo")) + "GenTest";
        // ServiceImpl name
        String serviceImplClassName = serviceClassName + "Impl";

        // flag for findIds query
        boolean hasFinder = false;
        String findMethod = "find" + Util.determineShortName(className) + "Ids";
        for (Method method: methods) {
            if (findMethod.equals(method.getName())) {
                hasFinder = true;
                break;
            }
        }

        if (hasFinder) {
            System.out.println("For QueryResults, copy an existing one and search and replace on the Object Definition name.");
//            QueryResultsGen queryResultsGenerator = new QueryResultsGen(className);
//            queryResultsGenerator.generate();
        }

        ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(serviceImplClassName, serviceInterface, boClassName, className, fields, methods, hasAttributes, hasFinder);
        generatedOutput.append(serviceImplGenerator.generate()).append("\n");

        ServiceImplTestGenerator serviceImplTestGenerator = new ServiceImplTestGenerator(serviceImplClassName + "GenTest", boClassName, className, fields, foreignKeyTypes, hasAttributes);
        generatedOutput.append(serviceImplTestGenerator.generate()).append("\n");

        IntegrationTestGenerator iTestGenerator = new IntegrationTestGenerator(boClassName.substring(0, boClassName.indexOf("Bo")) + "IntegrationGenTest", boClassName, className, fields, foreignKeyTypes, hasAttributes);
        generatedOutput.append(iTestGenerator.generate()).append("\n");

        if (System.getProperty("OUTPUT_FILE") != null) {
            FileUtils.writeStringToFile(new File(System.getProperty("OUTPUT_FILE")), generatedOutput.toString(), false); // overwrite
        }
    }

    /*
 ######   #######  ##    ## ######## ########     ###     ######  ########       #### ##     ## ########  ##
##    ## ##     ## ###   ##    ##    ##     ##   ## ##   ##    ##    ##           ##  ###   ### ##     ## ##
##       ##     ## ####  ##    ##    ##     ##  ##   ##  ##          ##           ##  #### #### ##     ## ##
##       ##     ## ## ## ##    ##    ########  ##     ## ##          ##           ##  ## ### ## ########  ##
##       ##     ## ##  ####    ##    ##   ##   ######### ##          ##           ##  ##     ## ##        ##
##    ## ##     ## ##   ###    ##    ##    ##  ##     ## ##    ##    ##           ##  ##     ## ##        ##
 ######   #######  ##    ##    ##    ##     ## ##     ##  ######     ##          #### ##     ## ##        ########
    */
    public static class Generator {

        private final String contractInterfaceName;
        private final String className;
        private final JCodeModel codeModel;
        private final Map<String, List<String>> enumsValues;

        public Generator(String contractInterfaceName, String className, Map<String, List<String>> enumsValues) {
            this.contractInterfaceName = contractInterfaceName;
            this.className = className;
            this.codeModel = new JCodeModel();
            this.enumsValues = new HashMap<String, List<String>>(enumsValues);
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {

            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
            Class<?> contractInterface = Class.forName(contractInterfaceName);
            classModel._implements(contractInterface);
            classModel._extends(AbstractDataTransferObject.class);

            List<FieldModel> fields = determineFields(contractInterface);
            if (className.endsWith("Attribute")) {
                // swap out KrmsAttributeDefinitionContract (JAXB doesnt' do interfaces)
                fields.remove(new FieldModel("attributeDefinition", Class.forName("org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract")));
                //                fields.remove(new FieldModel("attributeDefinition", Class.forName("org.kuali.rice.krms.api.repository.type.KrmsAttributeContract")));
                // keep existing order it is required by the annotations!
                int attrDefIdIndex = -1;
                for (int i = 0, s = fields.size(); i < s; i++) {
                    if (fields.get(i).fieldName.equals("attributeDefinitionId")) {
                        attrDefIdIndex = i;
                        break;
                    }
                }
                fields.add(attrDefIdIndex + 1, new FieldModel("attributeDefinition", Class.forName("org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition")));
            }

            renderClassJavadoc(classModel);
            renderConstantsClass(classModel);
            renderElementsClass(classModel, fields);
            renderClassLevelAnnotations(classModel, fields);
            renderFields(classModel, fields);
            renderFutureElementsField(classModel);
            renderPrivateJaxbConstructor(classModel, fields);
            renderBuilderConstructor(classModel, fields);
            renderGetters(classModel, fields);
            renderBuilderClass(classModel, fields, contractInterface, enumsValues);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        private void renderClassJavadoc(JDefinedClass classModel) {JDocComment javadoc = classModel.javadoc();
            String shortClassName = className;
            if (shortClassName.contains(".")) {
                shortClassName = shortClassName.substring(shortClassName.lastIndexOf(".") + 1, shortClassName.length());
            }
            javadoc.append("Generated using JVM arguments " + Util.getGenerationProperties() + "\n");
            javadoc.append("Concrete model object implementation, immutable. \nInstances can be (un)marshalled to and from XML.\n\n");
            javadoc.append("@see " + shortClassName + "Contract\n\n");
            javadoc.append(Util.CLASS_AUTHOR_JAVADOC);
        }

        private void renderConstantsClass(JDefinedClass classModel) throws Exception {

            // define constants class
            JDefinedClass constantsClass = classModel._class(JMod.STATIC, Util.CONSTANTS_CLASS_NAME);

            // generate the javadoc on the top of the Constants class
            JDocComment javadoc = constantsClass.javadoc();
            javadoc.append(Util.CONSTANTS_CLASS_JAVADOC);

            // render root element name
            JFieldVar rootElementField = constantsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.ROOT_ELEMENT_NAME_FIELD);
            rootElementField.init(JExpr.lit(Util.toLowerCaseFirstLetter(classModel.name())));

            // render type name
            JFieldVar typeNameField = constantsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.TYPE_NAME_FIELD);
            typeNameField.init(JExpr.lit(classModel.name() + Util.TYPE_NAME_SUFFIX));

        }

        private void renderElementsClass(JDefinedClass classModel, List<FieldModel> fields) throws Exception {

            // define constants class
            JDefinedClass elementsClass = classModel._class(JMod.STATIC, Util.ELEMENTS_CLASS_NAME);

            // generate the javadoc on the top of the Elements class
            JDocComment javadoc = elementsClass.javadoc();
            javadoc.append(Util.ELEMENTS_CLASS_JAVADOC);

            // go through each field and create a corresponding constant
            for (FieldModel fieldModel : fields) {
                if (Util.isCommonElement(fieldModel.fieldName)) {
                    continue;
                }
                JFieldVar elementFieldVar = elementsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.toConstantsVariable(fieldModel.fieldName));
                elementFieldVar.init(JExpr.lit(fieldModel.fieldName));
            }
        }

        private void renderClassLevelAnnotations(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
            JFieldRef constantsClass = classModel.staticRef(Util.CONSTANTS_CLASS_NAME);
            JFieldRef elementsClass = classModel.staticRef(Util.ELEMENTS_CLASS_NAME);
            JClass coreConstants = codeModel.ref(CoreConstants.class);
            JFieldRef commonElementsRef = coreConstants.staticRef("CommonElements");

            // XmlRootElement
            JAnnotationUse rootElementAnnotation = classModel.annotate(XmlRootElement.class);
            rootElementAnnotation.param("name", constantsClass.ref(Util.ROOT_ELEMENT_NAME_FIELD));

            // XmlAccessorType
            JAnnotationUse xmlAccessorTypeAnnotation = classModel.annotate(XmlAccessorType.class);
            xmlAccessorTypeAnnotation.param("value", XmlAccessType.NONE);

            // XmlType
            JAnnotationUse xmlTypeAnnotation = classModel.annotate(XmlType.class);
            xmlTypeAnnotation.param("name", constantsClass.ref(Util.TYPE_NAME_FIELD));
            JAnnotationArrayMember propOrderMember = xmlTypeAnnotation.paramArray("propOrder");
            for (FieldModel field : fields) {
                if (Util.isCommonElement(field.fieldName)) {
                    propOrderMember.param(commonElementsRef.ref(Util.toConstantsVariable(field.fieldName)));
                } else {
                    propOrderMember.param(elementsClass.ref(Util.toConstantsVariable(field.fieldName)));
                }
            }
            propOrderMember.param(commonElementsRef.ref("FUTURE_ELEMENTS"));
        }

        private void renderFields(JDefinedClass classModel, List<FieldModel> fields) throws Exception{
            for (FieldModel fieldModel : fields) {
                renderField(classModel, fieldModel);
            }
        }

        private void renderField(JDefinedClass classModel, FieldModel fieldModel) throws Exception{
            JFieldVar field = null;
            if ("attributes".equals(fieldModel.fieldName)) {
                field = classModel.field(JMod.PRIVATE | JMod.FINAL, newMapStringStringClass(codeModel), fieldModel.fieldName);
                JAnnotationUse adapter = field.annotate(XmlJavaTypeAdapter.class);
                adapter.param("value", MapStringStringAdapter.class);
//                adapter.param("required", false); // not found, the attribute that maps this is required false so hopeful this is okay not there
            } else {
                field = classModel.field(JMod.PRIVATE | JMod.FINAL, fieldModel.fieldType, fieldModel.fieldName);
            }
            JAnnotationUse annotation = field.annotate(XmlElement.class);
            if (Util.isCommonElement(fieldModel.fieldName)) {
                JClass coreConstants = codeModel.ref(CoreConstants.class);
                JFieldRef commonElementsRef = coreConstants.staticRef("CommonElements");
                annotation.param("name", commonElementsRef.ref(Util.toConstantsVariable(fieldModel.fieldName)));
            } else {
                JClass elementsClass = codeModel.ref(Util.ELEMENTS_CLASS_NAME);
                JFieldRef fieldXmlNameRef = elementsClass.staticRef(Util.toConstantsVariable(fieldModel.fieldName));
                annotation.param("name", fieldXmlNameRef);
            }
            annotation.param("required", false);
        }

        private void renderFutureElementsField(JDefinedClass classModel) throws Exception {
            JType collectionType = codeModel.parseType("java.util.Collection<org.w3c.dom.Element>");
            JFieldVar field = classModel.field(JMod.PRIVATE | JMod.FINAL, collectionType, "_futureElements");
            field.init(JExpr._null());
            JAnnotationUse annotation = field.annotate(SuppressWarnings.class);
            annotation.param("value", "unused");
            field.annotate(XmlAnyElement.class);
        }

        private void renderPrivateJaxbConstructor(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.constructor(JMod.PRIVATE);
            JBlock body = method.body();
            for (FieldModel fieldModel : fields) {
                body.directStatement("this." + fieldModel.fieldName + " = " + Util.getInitString(fieldModel.fieldType) + ";");
            }
            method.javadoc().append(Util.PRIVATE_JAXB_CONSTRUCTOR_JAVADOC);
        }

        private void renderBuilderConstructor(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.constructor(JMod.PRIVATE);
            method.param(codeModel.ref("Builder"), "builder");
            JBlock body = method.body();
            for (FieldModel fieldModel : fields) {
                if ("attributeDefinition".equals(fieldModel.fieldName)) {
                    body.directStatement("if (builder.getAttributeDefinition() != null) {");
                    body.directStatement("    this.attributeDefinition = builder.getAttributeDefinition().build();");
                    body.directStatement("} else {");
                    body.directStatement("    this.attributeDefinition = null;");
                    body.directStatement("}");
                } else {
                    body.directStatement("this." + fieldModel.fieldName + " = builder." + Util.generateGetter(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)) + ";");
                }
            }
            method.javadoc().append(Util.BUILDER_CONSTRUCTOR_JAVADOC);
        }

        private void renderGetters(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
            for (FieldModel fieldModel : fields) {
                JMethod getterMethod = null;
                JBlock methodBody = null;
                if ("attributes".equals(fieldModel.fieldName)) {
                    JClass returnClass = newMapStringStringClass(codeModel);

                    getterMethod = classModel.method(JMod.PUBLIC, returnClass, Util.generateGetterName(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)));
                    methodBody = getterMethod.body();
                } else if ("attributeDefinition".equals(fieldModel.fieldName)) {
                    // return use .Builder class
                    //                   getterMethod = classModel.method(JMod.PUBLIC, Class.forName(fieldModel.fieldType.getName() + ".Builder"), Util.generateGetterName(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)));
                    getterMethod = classModel.method(JMod.PUBLIC, fieldModel.fieldType, Util.generateGetterName(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)));
                    getterMethod.javadoc().append("// TODO change return type and field type of Builder method method and field with  " + fieldModel.fieldType.getName() + ".Builder");
                    methodBody = getterMethod.body();
                } else {
                    getterMethod = classModel.method(JMod.PUBLIC, fieldModel.fieldType, Util.generateGetterName(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)));
                    methodBody = getterMethod.body();
                }

                methodBody.directStatement("return this." + fieldModel.fieldName + ";");
                getterMethod.annotate(Override.class);
            }
        }

        private void renderBuilderClass(JDefinedClass classModel, List<FieldModel> fields, Class<?> contractInterface, Map<String, List<String>> enumsValues) throws Exception {

            // define constants class
            JDefinedClass builderClass = classModel._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, Util.BUILDER_CLASS_NAME);

            // create a literal version of the Builder class so that the code generator won't pre-pend Builder class references with outermost class
            JClass literalBuilderClass = codeModel.ref("Builder");

            // generate the javadoc on the top of the Elements class
            JDocComment javadoc = builderClass.javadoc();
            javadoc.append(Util.generateBuilderJavadoc(classModel.name(), contractInterface.getSimpleName()));

            builderClass._implements(contractInterface);
            builderClass._implements(ModelBuilder.class);
            builderClass._implements(Serializable.class);

            // render the builder fields
            for (FieldModel fieldModel : fields) {
                if ("attributes".equals(fieldModel.fieldName)) {
                    builderClass.field(JMod.PRIVATE, newMapStringStringClass(codeModel), fieldModel.fieldName);
                } else {
                    builderClass.field(JMod.PRIVATE, fieldModel.fieldType, fieldModel.fieldName);
                }
            }

            // render default empty constructor for builder
            JMethod constructor = builderClass.constructor(JMod.PRIVATE);
            List<String> params = new LinkedList<String>();
            List<FieldModel> sortedFields = sort(fields);

            constructor.body().directStatement("// TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods");
            for (FieldModel fieldModel : sortedFields) {
                if (Util.VALIDATED_FIELDS_NOT_BLANK.contains(fieldModel.fieldName)) {
                    if (!"id".equals(fieldModel.fieldName)) {
                        constructor.param(fieldModel.fieldType, fieldModel.fieldName);
                        constructor.body().directStatement("set" + Util.toUpperCaseFirstLetter(fieldModel.fieldName)  + "(" + fieldModel.fieldName + ");");
                        params.add(fieldModel.fieldName);
                    }
                } else if ("attributes".equals(fieldModel.fieldName)) {
                    constructor.body().directStatement("setAttributes(null); // setAttributes will create empty map for null");
                }
            }

            renderBuilderDefaultCreate(builderClass, literalBuilderClass, sortedFields);
            renderBuilderCreateContract(builderClass, literalBuilderClass, sortedFields, contractInterface, params);
            renderBuild(builderClass);
            renderGetters(builderClass, sortedFields);
            renderBuilderSetters(builderClass, sortedFields, enumsValues);
        }

        private void  renderBuilderDefaultCreate(JDefinedClass builderClass, JClass literalBuilderClass, List<FieldModel> fields) {
            JMethod createMethod = builderClass.method(JMod.PUBLIC | JMod.STATIC, literalBuilderClass, "create");
            JBlock createMethodBody = createMethod.body();

            String paramsBuilder = builderCreate(fields, createMethod);
            createMethodBody.directStatement("// TODO modify as needed to pass any required values and add them to the signature of the 'create' method");

            if (paramsBuilder.length() == 0) {
                createMethodBody.directStatement("return new Builder();");
            } else {
                createMethodBody.directStatement("return new Builder(" +  paramsBuilder + ");");
            }
        }

        private void renderBuilderCreateContract(JDefinedClass builderClass, JClass literalBuilderClass, List<FieldModel> fields, Class<?> contractInterface, List<String> params) {
            JMethod createContractMethod = builderClass.method(JMod.PUBLIC | JMod.STATIC, literalBuilderClass, "create");
            JVar contractParam = createContractMethod.param(contractInterface, "contract");
            JBlock body = createContractMethod.body();
            JConditional nullContractCheck = body._if(contractParam.eq(JExpr._null()));
            nullContractCheck._then().directStatement("throw new IllegalArgumentException(\"contract was null\");");
            body.directStatement("// TODO if create() is modified to accept required parameters, this will need to be modified");
            if (params.size() == 0) {
                body.directStatement("Builder builder = create();");
            } else {
                StringBuilder create = new StringBuilder();
                for (String param : params) {
                    create.append("contract.get" + Util.toUpperCaseFirstLetter(param) + "(), ");
                }
                body.directStatement("Builder builder = create(" + create.substring(0, create.length() - 2) + ");");
            }

            body.directStatement("builder." + Util.generateSetter("id", "contract." + Util.generateGetter("id", false)) + ";");
            for (FieldModel fieldModel : fields) {
                String fieldName = fieldModel.fieldName;
// this logic is done in setAttributes
//                if ("attributes".equals(fieldModel.fieldName)) {
//                    body.directStatement("if (builder.getAttributes() != null){");
//                    body.directStatement("    this.attributes = Collections.unmodifiableMap(new HashMap<String, String>(builder.getAttributes()));");
//                    body.directStatement("}");
//                    body.directStatement("else {");
//                    body.directStatement("    this.attributes = null;");
//                    body.directStatement("}");
//                } else
                if ("attributeDefinition".equals(fieldModel.fieldName)) {
                    body.directStatement("if (contract.getAttributeDefinition() != null) {");
                    body.directStatement("    builder.setAttributeDefinition(KrmsAttributeDefinition.Builder.create(contract.getAttributeDefinition()));");
                    body.directStatement("}");
                } else if (!Util.VALIDATED_FIELDS_NOT_BLANK.contains(fieldName)) {
                    body.directStatement("builder." + Util.generateSetter(fieldName, "contract." + Util.generateGetter(fieldName, Util.isBoolean(fieldModel.fieldType))) + ";");
                }
            }
            body.directStatement("return builder;");
        }

        private void renderBuild(JDefinedClass builderClass) {
            JMethod buildMethod = builderClass.method(JMod.PUBLIC, builderClass.outer(), "build");
            buildMethod.body().directStatement("return new " + builderClass.outer().name() + "(this);");
            buildMethod.javadoc().append("Builds an instance of a " + builderClass.outer().name()+  " based on the current state of the builder.\n\n@return the fully-constructed " + builderClass.outer().name() + ".");
        }

        private void renderBuilderSetters(JDefinedClass builderClass, List<FieldModel> fields, Map<String, List<String>> enumsValues) throws Exception {
            for (FieldModel fieldModel : fields) {
                String fieldName = fieldModel.fieldName;
                JMethod  setterMethod = builderClass.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(fieldName));
                if (Util.VALIDATED_FIELDS_NOT_BLANK.contains(fieldName)) {
                    setterMethod.param(fieldModel.fieldType, fieldName);
                    if ("id".equals(fieldModel.fieldName)) { // The Object's id can be null, but not blank
                        renderIsNotNullAndBlankIllegalArgumentExceptionGuard(fieldName, setterMethod);
                        String javadoc = Util.generateBuilderSetterJavadoc(fieldName) + ", may be null, representing the Object has not been persisted, but must not be blank.";
                        setterMethod.javadoc().append(javadoc);
                        setterMethod.javadoc().append("\n@throws IllegalArgumentException if the " + fieldName + " is blank");
                    } else if ("String".equals(fieldModel.fieldType.getSimpleName())) {
                        if (enumsValues.containsKey(Util.toUpperCaseFirstLetter(fieldName))) {
                            renderEnumTypeIllegalArgumentGuard(fieldName, setterMethod);
                        } else {
                            renderStringUtilsIsBlankIllegalArgumentExceptionGuard(fieldName, setterMethod);
                            String javadoc = Util.generateBuilderSetterJavadoc(fieldName) + ", must not be null or blank";
                            setterMethod.javadoc().append(javadoc);
                            setterMethod.javadoc().append("\n@throws IllegalArgumentException if the " + fieldName + " is null or blank");
                        }
                    } else {
                        renderIsNullIllegalArgumentExceptionGuard(fieldName, setterMethod);
                        String javadoc = Util.generateBuilderSetterJavadoc(fieldName) + ", must not be null";
                        setterMethod.javadoc().append(javadoc);
                        setterMethod.javadoc().append("\n@throws IllegalArgumentException if the " + fieldName + " is null");
                    }
                    setterMethod.body().directStatement("this." + fieldName + " = " + fieldName + ";");
                } else if ("attributes".equals(fieldName)) {
                    setterMethod.param(newMapStringStringClass(codeModel), fieldName);
                    setterMethod.body().directStatement("if (attributes == null){");
                    setterMethod.body().directStatement("    this.attributes = Collections.emptyMap();");
                    setterMethod.body().directStatement("} else {");
                    setterMethod.body().directStatement("    this.attributes = Collections.unmodifiableMap(attributes);");
                    setterMethod.body().directStatement("}");
                    setterMethod.javadoc().append("Sets the Map of attributes as name / value pairs.\n\n"
                            + "@param attributes a Map of name value String pairs representing the attributes.");
                } else if ("attributeDefinition".equals(fieldName)) {
                    setterMethod.param(newMapStringStringClass(codeModel), fieldName);
                    setterMethod.body().directStatement("    this.attributeDefinition = attributeDefinition;");
                    setterMethod.javadoc().append("// TODO change Builder param type and field to " + fieldModel.fieldType + ".Builder");
                } else if (!Util.UNVALIDATED_FIELD_SETS.contains(fieldName)) { // NOTICE: Util.UNVALIDATED_FIELD_SETS.contains(fieldName) must be last else if!
                    setterMethod.param(fieldModel.fieldType, fieldName);
                    setterMethod.body().directStatement("// TODO add validation of input value if required and throw IllegalArgumentException if needed");
                    setterMethod.body().directStatement("this." + fieldName + " = " + fieldName + ";");
                    setterMethod.javadoc().append(Util.generateBuilderSetterJavadoc(fieldName));
                } else { // Javadoc for Util.UNVALIDATED_FIELD_SETS should always be last else
                    setterMethod.param(fieldModel.fieldType, fieldName);
                    setterMethod.body().directStatement("this." + fieldName + " = " + fieldName + ";");
                    setterMethod.javadoc().append(Util.generateBuilderSetterJavadoc(fieldName));
                }
            }
        }

        private void renderEnumTypeIllegalArgumentGuard(String fieldName, JMethod setterMethod) {
//            setterMethod.body().directStatement("if (org.apache.commons.lang.StringUtils.isBlank(" + fieldName + ")){ return; }");
            setterMethod.body().directStatement("if (org.apache.commons.lang.StringUtils.isBlank(" + fieldName + ")){ throw new IllegalArgumentException(\"" + fieldName + " type code is blank.\"); }");
            setterMethod.body().directStatement("if (!(" + Util.toUpperCaseFirstLetter(fieldName) + ".VALID_TYPE_CODES" + ".contains(" + fieldName + "))) {");
            setterMethod.body().directStatement("    throw new IllegalArgumentException(\"invalid " + fieldName + " value\");");
            setterMethod.body().directStatement("}");
        }

    }

    /*
 ######   #######  ##    ## ######## ########     ###     ######  ########       ######## ########  ######  ########
##    ## ##     ## ###   ##    ##    ##     ##   ## ##   ##    ##    ##             ##    ##       ##    ##    ##
##       ##     ## ####  ##    ##    ##     ##  ##   ##  ##          ##             ##    ##       ##          ##
##       ##     ## ## ## ##    ##    ########  ##     ## ##          ##             ##    ######    ######     ##
##       ##     ## ##  ####    ##    ##   ##   ######### ##          ##             ##    ##             ##    ##
##    ## ##     ## ##   ###    ##    ##    ##  ##     ## ##    ##    ##             ##    ##       ##    ##    ##
 ######   #######  ##    ##    ##    ##     ## ##     ##  ######     ##             ##    ########  ######     ##
     */
    public static class ContractTestGenerator {

        private final String contractInterfaceName;
        private final String className;
        private final String shortClassNameUnderTest;
        private final Class classUnderTest;
        private final JCodeModel codeModel;
        private final List<FieldModel> fields;
        private final Map<String, String> foreignKeyTypes;
        private final Map<String, List<String>> enumsValues;        

        public ContractTestGenerator(String contractInterfaceName, String className, List<FieldModel> fields, Map<String, String> foreignKeyTypes, Map<String, List<String>> enumsValues) throws Exception{
            this.contractInterfaceName = contractInterfaceName;
            this.className = className;
            String krmsWorkAround = "";
            if (Util.IS_KRMS) {
                krmsWorkAround = "Definition";
            }
            this.classUnderTest = Class.forName(className.substring(0, className.indexOf("GenTest"))+ krmsWorkAround);
            this.shortClassNameUnderTest = classUnderTest.getSimpleName();
            this.codeModel = new JCodeModel();
            this.fields = new LinkedList<FieldModel>(fields);
            this.foreignKeyTypes = new HashMap<String, String>(foreignKeyTypes);
            this.enumsValues = new HashMap<String, List<String>>(enumsValues);
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);

            Class<?> contractInterface = Class.forName(contractInterfaceName);
            List<FieldModel> fields = determineFields(contractInterface);
            List<FieldModel> sortedFields = sort(fields);

            renderClassAuthorJavadoc(classModel);
            renderFields(classModel, sortedFields, enumsValues);
            renderBuilderCreateFailAllNullTest(classModel);
            renderSetValidationFailures(classModel, sortedFields);
            renderCreateTest(classModel, sortedFields);
            renderCreateAndBuildTest(classModel, sortedFields);
            renderXmlMarshalingTest(classModel, sortedFields);
            renderAssertXmlMarshaling(classModel, sortedFields);
            renderBuildFull(classModel, sortedFields, foreignKeyTypes);
            if (!foreignKeyTypes.isEmpty()) {
                renderBuildFullFKs(classModel, sortedFields, foreignKeyTypes);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        private void renderFields(JDefinedClass classModel, List<FieldModel> fields, Map<String, List<String>> enumsValues) {
            String xmlValue = "paste xml output here";
            JFieldVar xmlField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, String.class, "XML", JExpr.lit(xmlValue));
            for (FieldModel field :  fields) {
                if  (enumsValues.containsKey(Util.toUpperCaseFirstLetter(field.fieldName))) {
                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(Util.toUpperCaseFirstLetter(field.fieldName)
                            + "." + (enumsValues.get(Util.toUpperCaseFirstLetter(field.fieldName))).get(0) + ";")); // as Type
//                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(Util.toUpperCaseFirstLetter(field.fieldName)
//                            + "." + (enumsValues.get(Util.toUpperCaseFirstLetter(field.fieldName))).get(0) + ".getCode();")); // as String
                    newField.javadoc().append("TODO remove the quotes around the assignment and figure out how to assign this via codeModel.");
                } else if ("String".equals(field.fieldType.getSimpleName())) {
                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(Util.toConstantsVariable(field.fieldName)));
                } else if ("Long".equals(field.fieldType.getSimpleName())) {
                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(Util.nextLong()));
                } else if ("boolean".equals(field.fieldType.getSimpleName())) {
                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(false));
                } else if ("Integer".equals(field.fieldType.getSimpleName())) {
                    JFieldVar newField = classModel.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, field.fieldType, Util.toConstantsVariable(field.fieldName), JExpr.lit(-1));
                }
            }
        }

        private void renderBuilderCreateFailAllNullTest(JDefinedClass classModel) throws Exception {
            String paramLine = paramConstantsLine("null", fields);
            if (!paramLine.isEmpty()) {
                JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + shortClassNameUnderTest
                        + "_Builder_create_fail_all_null");
                JAnnotationUse annotation = method.annotate(Test.class);
                annotation.param("expected", IllegalArgumentException.class);
                method.body().directStatement(shortClassNameUnderTest + ".Builder.create(" + paramLine + ");");
            }
        }

        private void renderSetValidationFailures(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
            for (FieldModel field :  fields) {
                if (Util.VALIDATED_FIELDS_NOT_BLANK.contains(field.fieldName)) {
                    if ("id".equals(field.fieldName)) { // IDs can be null, but not blank
                        generateValidNullSetTest(classModel, fields, field.fieldName, "null", "null");
                    } else {
                        generateInvalidSetTest(classModel, fields, field.fieldName, "null", "null");
                    }
                    if ("String".equals(field.fieldType.getSimpleName())) {
                        generateInvalidSetTest(classModel, fields, field.fieldName, "\"\"", "empty");
                        generateInvalidSetTest(classModel, fields, field.fieldName, "\"    \"", "whitespace");
                    }
                }
            }
        }

        private void renderCreateTest(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + shortClassNameUnderTest
                    + "_Builder_create");
            String paramLine = paramConstantsLine(null, fields);
            method.body().directStatement(shortClassNameUnderTest + ".Builder.create(" + paramLine + ");");
            JAnnotationUse annotation = method.annotate(Test.class);
        }

        private void renderCreateAndBuildTest(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID,
                    "test_" + shortClassNameUnderTest + "_Builder_create_and_build");
            method.body().directStatement(shortClassNameUnderTest
                    + ".Builder builder = "
                    + shortClassNameUnderTest
                    + ".Builder.create("
                    + paramConstantsLine(null, fields)
                    + ");");
            method.body().directStatement("builder.build();");
            JAnnotationUse annotation = method.annotate(Test.class);
        }

        private void renderAssertXmlMarshaling(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "assertXmlMarshaling");
            method.param(Object.class, Util.toLowerCaseFirstLetter(shortClassNameUnderTest));
            method.param(String.class, "expectedXml");
            method._throws(Exception.class);
            method.body().directStatement("JAXBContext jc = JAXBContext.newInstance(" + shortClassNameUnderTest + ".class);\n");
            method.body().directStatement("Marshaller marshaller = jc.createMarshaller();");
            method.body().directStatement("StringWriter stringWriter = new StringWriter();");
            method.body().directStatement("marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);");
            method.body().directStatement("// marshaller.setProperty(\"com.sun.xml.internal.bind.namespacePrefixMapper\", new CustomNamespacePrefixMapper());");
            method.body().directStatement("marshaller.marshal(" + Util.toLowerCaseFirstLetter(shortClassNameUnderTest) + ", stringWriter);");
            method.body().directStatement("String xml = stringWriter.toString();\n");
            method.body().directStatement("System.out.println(xml); // TODO run test, paste xml output into XML, comment out this line.\n");

            method.body().directStatement("Unmarshaller unmarshaller = jc.createUnmarshaller();");
            method.body().directStatement("Object actual = unmarshaller.unmarshal(new StringReader(xml));");
            method.body().directStatement("Object expected = unmarshaller.unmarshal(new StringReader(expectedXml));");
            method.body().directStatement("Assert.assertEquals(expected, actual);");
        }
        
        private void renderXmlMarshalingTest(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + shortClassNameUnderTest
                    + "_xml_marshaling");
            method._throws(Exception.class);
            method.body().directStatement(shortClassNameUnderTest + " " + Util.toLowerCaseFirstLetter(
                    shortClassNameUnderTest)
                + " = buildFull" + shortClassNameUnderTest + "();\n");
            method.body().directStatement("assertXmlMarshaling(" + Util.toLowerCaseFirstLetter(shortClassNameUnderTest) + ", XML);");
            JAnnotationUse annotation = method.annotate(Test.class);
        }

        private void renderBuildFull(JDefinedClass classModel, List<FieldModel> fields, Map<String, String> foreignKeyTypes) throws Exception {
            JMethod method = classModel.method(JMod.PUBLIC | JMod.STATIC, classUnderTest, "buildFull" + shortClassNameUnderTest);

            // call foreign key type's Test.buildFull to satisfy dependencies.
            for (FieldModel field: fields) {
                if (foreignKeyTypes.keySet().contains(field.fieldName)) {
                    String n = field.fieldName;
                    if (field.fieldName.endsWith("Id")) {
                        n = n.substring(0, n.length()-2);
                    }
                    final String s = Util.determineShortName(foreignKeyTypes.get(field.fieldName));
                    // we don't use s in one place below, due to the KRMS naming mistake.
                    method.body().directStatement(foreignKeyTypes.get(field.fieldName) + " " + n + " = " + s + "GenTest.buildFull" + s + "();");
                }
            }
            // call our class's under test Test.buildFull
            method.body().directStatement(
                    shortClassNameUnderTest + ".Builder builder = " + shortClassNameUnderTest + ".Builder.create(" + paramForeignKeysAndConstantsLine(
                    null, fields) + ");");
            method.body().directStatement("builder.setId(ID);");
            method.body().directStatement(shortClassNameUnderTest + " " + Util.toLowerCaseFirstLetter(
                    shortClassNameUnderTest) + " = builder.build();");
            method.body().directStatement("return " + Util.toLowerCaseFirstLetter(shortClassNameUnderTest) + ";");
        }

        // kinda ugly given the next 2 methods....
        String paramForeignKeysAndConstantsLine(String prefix, List<FieldModel> fields) {
            if (prefix == null) {
                prefix = "";
            }
            StringBuilder paramLine = new StringBuilder();
            String createParams = builderCreate(fields, null);
            String token = null;
            if (createParams.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(createParams, ",");
                for (int i = 0, s = tokenizer.countTokens(); i < s; i++) {
                    if (prefix.equals("null")) {
                        paramLine.append("null, ");
                    } else {
                        token = tokenizer.nextToken().trim();
                        if (foreignKeyTypes.containsKey(token)) {
                            if (token.endsWith("Id")) {
                                token = token.substring(0, token.length() -  2);
                            }
                            paramLine.append(prefix + token + ".getId(), ");
                        } else if (!"attributes".equals(token)) {
                            paramLine.append(prefix + Util.toConstantsVariable(token) + ", ");
                        }
                    }
                }
            }
            if (paramLine.toString().endsWith(", ")) {
                paramLine = new StringBuilder(paramLine.toString().substring(0, paramLine.toString().length() - 2));
            }
            return paramLine.toString();
        }

        String paramConstantsLine(String prefix, List<FieldModel> fields) {
            if (prefix == null) {
                prefix = "";
            }
            StringBuilder paramLine = new StringBuilder();
            String createParams = builderCreate(fields, null);
            String token = null;
            if (createParams.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(createParams, ",");
                for (int i = 0, s = tokenizer.countTokens(); i < s; i++) {
                    if (prefix.equals("null")) {
                        paramLine.append("null, ");
                    } else {
                        token = tokenizer.nextToken().trim();
                        if (!"attributes".equals(token)) {
                            paramLine.append(prefix + Util.toConstantsVariable(token) + ", ");
                        }
                    }
                }
            }
            if (paramLine.toString().endsWith(", ")) {
                paramLine = new StringBuilder(paramLine.toString().substring(0, paramLine.toString().length() - 2));
            }
            return paramLine.toString();
        }

        // like paramForeignKeysAndConstantsLine without the constants
        String paramForeignKeyObjectsLine(String prefix, List<FieldModel> fields) {
            if (prefix == null) {
                prefix = "";
            }
            StringBuilder paramLine = new StringBuilder();
            String createParams = builderCreate(fields, null);
            String token = null;
            if (createParams.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(createParams, ",");
                for (int i = 0, s = tokenizer.countTokens(); i < s; i++) {
                    if (prefix.equals("null")) {
                        paramLine.append("null, ");
                    } else {
                        token = tokenizer.nextToken().trim();
                        if (foreignKeyTypes.containsKey(token)) {
                            if (token.endsWith("Id")) {
                                token = token.substring(0, token.length() -  2);
                                paramLine.append(prefix + token + ", ");
                            }
                        }
                    }
                }
            }
            if (paramLine.toString().endsWith(", ")) {
                paramLine = new StringBuilder(paramLine.toString().substring(0, paramLine.toString().length() - 2));
            }
            return paramLine.toString();
        }
        private void renderBuildFullFKs(JDefinedClass classModel, List<FieldModel> fields, Map<String, String> foreignKeyTypes) throws Exception {
            JMethod method = classModel.method(JMod.PUBLIC | JMod.STATIC, classUnderTest, "buildFullFK" + shortClassNameUnderTest);

            method.body().directStatement("// TODO change the Object type of the parameters");
            // call foreign key type's Test.buildFull to satisfy dependencies.
            for (FieldModel field: fields) {
                if (foreignKeyTypes.keySet().contains(field.fieldName)) {
                    String n = field.fieldName;
                    if (field.fieldName.endsWith("Id")) {
                        n = n.substring(0, n.length()-2);
                    }
//                    final String s = Util.determineShortName(foreignKeyTypes.get(field.fieldName));
                    method.param(Object.class, n);
                }
            }
            // call our class's under test Test.buildFull
            method.body().directStatement(
                    shortClassNameUnderTest + ".Builder builder = " + shortClassNameUnderTest + ".Builder.create(" + paramForeignKeysAndConstantsLine(
                            null, fields) + ");");
            method.body().directStatement("builder.setId(ID);");
            method.body().directStatement(shortClassNameUnderTest + " " + Util.toLowerCaseFirstLetter(
                    shortClassNameUnderTest) + " = builder.build();");
            method.body().directStatement("return " + Util.toLowerCaseFirstLetter(shortClassNameUnderTest) + ";");
        }

        void generateInvalidSetTest(JDefinedClass classModel, List<FieldModel> fields, String validatedField,
                String invalidValue, String invalidName) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + shortClassNameUnderTest
                        + "_set_validation_" + validatedField + "_fail_" + invalidName);
            method.body().directStatement(
                    shortClassNameUnderTest + ".Builder builder = " + shortClassNameUnderTest + ".Builder.create(" + paramConstantsLine(
                    null, fields) + ");");
            method.body().directStatement("builder." + Util.generateSetterName(validatedField) + "(" + invalidValue + ");");
            JAnnotationUse annotation = method.annotate(Test.class);
            annotation.param("expected", IllegalArgumentException.class);
        }

        void generateValidNullSetTest(JDefinedClass classModel, List<FieldModel> fields, String validatedField,
                String invalidValue, String invalidName) {
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + shortClassNameUnderTest
                    + "_set_validation_" + validatedField + "_success_" + invalidName);
            method.body().directStatement(
                    shortClassNameUnderTest + ".Builder builder = " + shortClassNameUnderTest + ".Builder.create(" + paramConstantsLine(
                            null, fields) + ");");
            method.body().directStatement("builder."
                    + Util.generateSetterName(validatedField)
                    + "("
                    + invalidValue
                    + ");");
            JAnnotationUse annotation = method.annotate(Test.class);
        }


    }

    /*

                                                    ########   #######
                                                    ##     ## ##     ##
                                                    ##     ## ##     ##
                                                    ########  ##     ##
                                                    ##     ## ##     ##
                                                    ##     ## ##     ##
                                                    ########   #######


        Bo is currently implemented to use Sets rather than Lists to avoid an ojb exception
     */
    public static class BoGenerator {
        private final String contractInterfaceName;
        private final String className;
        private final String boClassName;
        private final String narrowTo;
        private final JCodeModel codeModel;
        private final boolean hasAttributes;

        public BoGenerator(String contractInterfaceName, String boClassName, String className, boolean hasAttributes) {
            this.contractInterfaceName = contractInterfaceName;
            this.className = className;
            this.codeModel = new JCodeModel();
            this.boClassName = boClassName;
            this.hasAttributes = hasAttributes;
            if (boClassName.endsWith("KrmsTypeBo")) {
                this.narrowTo = (boClassName.substring(0, boClassName.indexOf("Bo")) + "AttributeBo").replace(".type", "");
            } else {
                this.narrowTo = boClassName.substring(0, boClassName.indexOf("Bo")) + "AttributeBo";
            }
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        byte[] generateJava() throws Exception {

            JDefinedClass classModel = codeModel._class(JMod.PUBLIC, boClassName, ClassType.CLASS);
            // Common attribute base class seems to cause issues with how to set the values while they are private

            Class<?> contractInterface = Class.forName(contractInterfaceName);
            classModel._implements(contractInterface);

            List<FieldModel> fields = determineFields(contractInterface);

            if (className.endsWith("AttributeBo")) {
                // class as string to avoid compile time dependency.
                classModel._extends(Class.forName("org.kuali.rice.krms.impl.repository.BaseJavaAttributeBo")); // KRMS should this be promoted?
                // remove fields defined in BaseJavaAttributeBo.class
                List<FieldModel> baseFields = determineFields(Class.forName("org.kuali.rice.krms.impl.repository.BaseJavaAttributeBo"));
                fields.removeAll(baseFields);
            } else {
                classModel._extends(PersistableBusinessObjectBase.class);
            }

            Class boClass = Class.forName(boClassName);
            String baseClassName = Util.determineShortName(boClassName.substring(0, boClassName.indexOf("Bo")));

            renderClassJavadoc(classModel);
            renderFields(classModel, fields, codeModel, false);
            renderConstructor(classModel, fields);
            renderGetters(classModel, fields);
            renderSetters(classModel, fields);
            renderTo(classModel, Class.forName(contractInterfaceName.replace("Contract", "")));
            renderFrom(classModel, fields, Class.forName(boClassName), Class.forName(className), baseClassName);
            renderGetNewId(classModel);
            renderSetSequenceAccessorService(classModel);
            if (hasAttributes) {
//                renderBuildAttributeCollection(classModel, baseClassName, Class.forName(dtoClassName), className, codeModel);
                renderAttributes(classModel, baseClassName);
                String attributeBoClassName = boClassName.toString().replace("Bo", "AttributeBo");
                renderBuildAttributeCollection(classModel, baseClassName, Class.forName(attributeBoClassName), Class.forName(contractInterface.getName().replace("Contract", "")), className, codeModel, JMod.PRIVATE | JMod.STATIC);
                renderBuildAttributeBoSet(classModel, Class.forName(attributeBoClassName), Class.forName(contractInterface.getName().replace("Contract", "")), codeModel, JMod.PRIVATE | JMod.STATIC);
                renderBuildAttributeBoList(classModel, Class.forName(attributeBoClassName), Class.forName(contractInterface.getName().replace("Contract", "")), codeModel, JMod.PRIVATE | JMod.STATIC);
                renderKrmsAttributeDefinitionService(classModel);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();
        }

        void renderClassJavadoc(JDefinedClass classModel) {
            JDocComment javadoc = classModel.javadoc();
            javadoc.append(Util.generateBoClassJavadoc(classModel.name()));
        }

        void renderFields(JDefinedClass classModel, List<FieldModel> fields, JCodeModel codeModel, boolean xmlAnnotate) throws Exception {
            for (FieldModel fieldModel : fields) {
                renderField(classModel, fieldModel, codeModel, xmlAnnotate);
            }
            JFieldVar sas = classModel.field(JMod.PRIVATE, Class.forName("org.kuali.rice.krad.service.SequenceAccessorService"), "sequenceAccessorService");
        }

        void renderField(JDefinedClass classModel, FieldModel fieldModel, JCodeModel codeModel, boolean xmlAnnotate) {
//            JFieldVar field = classModel.field(JMod.PRIVATE, fieldModel.fieldType, fieldModel.fieldName);
            JFieldVar field = null;
            if ("attributes".equals(fieldModel.fieldName)) {
                field = classModel.field(JMod.PRIVATE, newMapStringStringClass(codeModel), fieldModel.fieldName);
            } else {
                field = classModel.field(JMod.PRIVATE, fieldModel.fieldType, fieldModel.fieldName);
            }

            JAnnotationUse annotation = null;
            if (xmlAnnotate) {
                annotation = field.annotate(XmlElement.class);
            }
            if (Util.isCommonElement(fieldModel.fieldName)) {
                JClass coreConstants = codeModel.ref(CoreConstants.class);
                JFieldRef commonElementsRef = coreConstants.staticRef("CommonElements");
                if (xmlAnnotate) {
                    annotation.param("name", commonElementsRef.ref(Util.toConstantsVariable(fieldModel.fieldName)));
                }
            } else {
                JClass elementsClass = codeModel.ref(Util.ELEMENTS_CLASS_NAME);
                JFieldRef fieldXmlNameRef = elementsClass.staticRef(Util.toConstantsVariable(fieldModel.fieldName));
                if (xmlAnnotate) {
                    annotation.param("name", fieldXmlNameRef);
                }
            }
            if (xmlAnnotate) {
                annotation.param("required", false);
            }
        }

        void renderConstructor(JDefinedClass classModel, List<FieldModel> fields) {
            JMethod method = classModel.constructor(JMod.PUBLIC);
            JBlock body = method.body();
            method.javadoc().append("Default Constructor");
        }

        void renderGetters(JDefinedClass classModel, List<FieldModel> fields) {
            for (FieldModel fieldModel : fields) {
                if (hasAttributes && "attributes".equals(fieldModel.fieldName)) {
                    // Empty ifs are bad, but the negative seemed worse.
                } else if (isAttributeBoParentField(fieldModel)) {
                    // KRMS extends BaseJavaAttributeBo which implements these
                } else {
                    JMethod getterMethod = classModel.method(JMod.PUBLIC, fieldModel.fieldType, Util.generateGetterName(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)));
                    JBlock methodBody = getterMethod.body();
                    methodBody.directStatement("return this." + fieldModel.fieldName + ";");
                    getterMethod.annotate(Override.class);
                }
            }
        }

        private boolean isAttributeBoParentField(FieldModel fieldModel) {
            return className.endsWith("AttributeBo")
                       && ("attributeDefinitionId".equals(fieldModel.fieldName)
                           || "attributeDefinition".equals(fieldModel.fieldName)
                           || "value".equals(fieldModel.fieldName)
                       );
        }

        void renderSetters(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
            for (FieldModel fieldModel : fields) {
                if (hasAttributes && "attributes".equals(fieldModel.fieldName)) {
                    // Empty ifs are bad, but the negative seemed worse.
                } else {
                    JMethod setterMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(
                            fieldModel.fieldName));
                    setterMethod.param(fieldModel.fieldType, fieldModel.fieldName);
                    JBlock methodBody = setterMethod.body();
                    methodBody.directStatement("this." + fieldModel.fieldName + " = " + fieldModel.fieldName + ";");
                    setterMethod.javadoc().append(Util.generateBuilderSetterJavadoc(fieldModel.fieldName));
                }
            }
            if (hasAttributes) {
                if (false) { // attributes need to be Sets if the OJB def uses HashSet
//                if (!Util.USE_SET) {
                    String baseName = Util.shortClassName(className);
                    JMethod setterMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(
                            "AttributeBos"));
                    JClass paramClass = narrowedList(Class.forName(narrowTo), codeModel);
                    setterMethod.param(paramClass, "attributeBos");
                    JBlock methodBody = setterMethod.body();
                    baseName = baseName.substring(0, baseName.indexOf("Bo"));
                    methodBody.directStatement("this.attributeBos = new LinkedList<" +  baseName + "AttributeBo>(attributeBos);");
                    setterMethod.javadoc().append(Util.generateBuilderSetterJavadoc("AttributeBos"));

                    JMethod setterSetMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(
                            "AttributeBos"));
                    JClass paramSetClass = narrowedSet(Class.forName(narrowTo), codeModel);
                    setterSetMethod.param(paramSetClass, "attributeBos");
                    JBlock methodSetBody = setterSetMethod.body();
                    methodSetBody.directStatement("this.attributeBos = new LinkedList<" +  baseName + "AttributeBo>(attributeBos);");
                    setterSetMethod.javadoc().append(Util.generateBuilderSetterJavadoc("AttributeBos"));
                } else {
                    String baseName = Util.shortClassName(boClassName);
                    JMethod setterMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(
                            "AttributeBos"));
                    JClass paramClass = narrowedList(Class.forName(narrowTo), codeModel);

                    setterMethod.param(paramClass, "attributeBos");
                    JBlock methodBody = setterMethod.body();
                    baseName = baseName.substring(0, baseName.indexOf("Bo"));
                    methodBody.directStatement("this.attributeBos = new HashSet<" +  baseName + "AttributeBo>(attributeBos);");
                    setterMethod.javadoc().append(Util.generateBuilderSetterJavadoc("AttributeBos"));

                    JMethod setterSetMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(
                            "AttributeBos"));
                    JClass paramSetClass = narrowedSet(Class.forName(narrowTo), codeModel);
                    setterSetMethod.param(paramSetClass, "attributeBos");
                    JBlock methodSetBody = setterSetMethod.body();
                    methodSetBody.directStatement("this.attributeBos = new HashSet<" +  baseName + "AttributeBo>(attributeBos);");
                    setterSetMethod.javadoc().append(Util.generateBuilderSetterJavadoc("AttributeBos"));
                }
            }
        }

        void renderTo(JDefinedClass classModel, Class dtoClass) {
            // Can't use createMethod here because of dependency on the Bo class we are generating.
            JMethod method = classModel.method(JMod.PUBLIC | JMod.STATIC, dtoClass, "to");
            method.param(classModel, Util.toLowerCaseFirstLetter(classModel.name()));
            JBlock methodBody = method.body();
            methodBody.directStatement("if (" + Util.toLowerCaseFirstLetter(classModel.name()) + " == null) { return null; }");
            methodBody.directStatement("return " + dtoClass.getSimpleName() + ".Builder.create(" + Util.toLowerCaseFirstLetter(classModel.name()) + ").build();");
            method.javadoc().append(Util.generateToJavadoc(classModel.name(), dtoClass.getSimpleName()));
        }

        void renderFrom(JDefinedClass classModel, List<FieldModel> fields, Class boClass, Class dtoClass, String baseClassName) {
            // Can't use createMethod here because of dependency on the Bo class we are generating.
            JMethod method = classModel.method(JMod.PUBLIC | JMod.STATIC, boClass, "from");
            method.param(dtoClass, Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())));

            JBlock methodBody = method.body();
            methodBody.directStatement("if (" + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())) + " == null) return null;");
            methodBody.directStatement(boClass.getSimpleName() + " " + Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + " = new " + (boClass.getSimpleName()) + "();");

            for (FieldModel fieldModel : fields) {
                if (!Util.COLLECTION_CLASSES.contains(fieldModel.fieldType.getSimpleName())) {
                    methodBody.directStatement(Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + ".set" + Util.toUpperCaseFirstLetter(fieldModel.fieldName) + "("
                            + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())) + "."
                            + Util.generateGetter(fieldModel.fieldName, Util.isBoolean(fieldModel.fieldType)) + ");");
                }
            }

            methodBody.directStatement("// TODO collections, etc.");
            if (hasAttributes) {
                methodBody.directStatement(Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + ".setAttributeBos(buildAttributeBoSet("
                        + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())) + "));");
                methodBody.directStatement("//" + Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + ".setAttributeBos(buildAttributeBoList("
                        + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())) + "));");
            }

            methodBody.directStatement("return " + Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + ";");
            method.javadoc().append(Util.generateFromJavadoc(boClass.getSimpleName(), dtoClass.getSimpleName()));
        }


        void renderGetNewId(JDefinedClass classModel) throws Exception{
            JMethod method = classModel.method(JMod.PRIVATE, String.class, "getNewId");
            method.param(String.class, "table");
            method.param(Class.class, "clazz");
            JBlock methodBody = method.body();

            methodBody.directStatement("if (sequenceAccessorService == null) {");
            methodBody.directStatement(
                    "    sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();");
            methodBody.directStatement("}");
            methodBody.directStatement(
                    "Long id = sequenceAccessorService.getNextAvailableSequenceNumber(table, clazz);");
            methodBody.directStatement("return id.toString();");

            method.javadoc().append("Returns the next available id for the given table and class.\n"
                    + "@return String the next available id for the given table and class.");
        }

        void renderSetSequenceAccessorService(JDefinedClass classModel) throws Exception {
            // don't use the createMethod with this, else sequenceAccessorService sets itself and not the one in the object
            JMethod method = classModel.method(JMod.PUBLIC, codeModel.VOID, "setSequenceAccessorService");
            method.param(Class.forName("org.kuali.rice.krad.service.SequenceAccessorService"), "sas");
            JBlock methodBody = method.body();
            methodBody.directStatement("sequenceAccessorService = sas;");
            method.javadoc().append("Set the SequenceAccessorService, useful for testing.\n"
                    + "@param sas SequenceAccessorService to use for getNewId.");

            JMethod getMethod = classModel.method(JMod.PUBLIC, Class.forName("org.kuali.rice.krad.service.SequenceAccessorService"), "getSequenceAccessorService");
            getMethod.body().directStatement("return sequenceAccessorService;");
        }

        void renderAttributes(JDefinedClass classModel, String baseClassName) throws Exception {
//            JClass fieldClass = narrowedList(Class.forName(className.substring(0, className.indexOf("Bo")) + "AttributeBo"), codeModel);
//            if (Util.USE_SET) {
                  JClass fieldClass = narrowedSet(Class.forName(narrowTo), codeModel);
//            }

            JFieldVar field = classModel.field(JMod.PRIVATE, fieldClass, "attributeBos");

            JMethod method = classModel.method(JMod.PUBLIC, newMapStringStringClass(codeModel), "getAttributes");
            JBlock methodBody = method.body();
            methodBody.directStatement("if (attributeBos == null) return Collections.emptyMap();\n");
            methodBody.directStatement("HashMap<String, String> attributes = new HashMap<String, String>(attributeBos.size());");
            methodBody.directStatement("for (" + baseClassName + "AttributeBo attr: attributeBos) {");
            methodBody.directStatement("    attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());");
            methodBody.directStatement("}");
            methodBody.directStatement("return attributes;");
            method.annotate(Override.class);

            JMethod setter = createMethodWithParam(classModel, codeModel.VOID , newMapStringStringClass(codeModel), "attributes", "setAttributes");
            JBlock setterBody = setter.body();
//            if (Util.USE_SET) {
                setterBody.directStatement("this.attributeBos  = new HashSet<" + baseClassName + "AttributeBo>();");
//            } else {
//                setterBody.directStatement("this.attributeBos  = new LinkedList<" + baseClassName + "AttributeBo>();");
//            }
            setterBody.directStatement("if (!org.apache.commons.lang.StringUtils.isBlank(this.typeId)) {");
//            setterBody.directStatement("    buildAttributes(im, this.attributeBos);");

            setterBody.directStatement("    List<KrmsAttributeDefinition> attributeDefinitions = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().findAttributeDefinitionsByType(this.getTypeId());");
            setterBody.directStatement("    Map<String, KrmsAttributeDefinition> attributeDefinitionsByName = new HashMap<String, KrmsAttributeDefinition>(attributeDefinitions.size());");
            setterBody.directStatement("    if (attributeDefinitions != null) for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {");
            setterBody.directStatement("        attributeDefinitionsByName.put(attributeDefinition.getName(), attributeDefinition);");
            setterBody.directStatement("    }");
            setterBody.directStatement("    for (Map.Entry<String, String> attr : attributes.entrySet()) {");
            setterBody.directStatement("        KrmsAttributeDefinition attributeDefinition = attributeDefinitionsByName.get(attr.getKey());");
            setterBody.directStatement("        " + baseClassName + "AttributeBo attributeBo = new "  + baseClassName + "AttributeBo();");
            setterBody.directStatement("        attributeBo.set"  + baseClassName + "Id(this.getId());");
            setterBody.directStatement("        attributeBo.setAttributeDefinitionId((attributeDefinition == null) ? null : attributeDefinition.getId());");
            setterBody.directStatement("        attributeBo.setValue(attr.getValue());");
            setterBody.directStatement("        attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attributeDefinition));");
            setterBody.directStatement("        attributeBos.add(attributeBo);");
            setterBody.directStatement("    }");
            setterBody.directStatement("}");
            setter.javadoc().append("TODO");
        }
        void renderKrmsAttributeDefinitionService(JDefinedClass classModel) {
            JFieldVar ads = classModel.field(JMod.PRIVATE | JMod.STATIC, KrmsAttributeDefinitionService.class, "attributeDefinitionService");
            JMethod setMethod = classModel.method(JMod.PUBLIC | JMod.STATIC, codeModel.VOID, "setAttributeDefinitionService");
            setMethod.param(KrmsAttributeDefinitionService.class, "attributeDefinitionService");
            setMethod.body().directStatement("attributeDefinitionService = attributeDefinitionService;");

            JMethod getMethod = classModel.method(JMod.PUBLIC | JMod.STATIC, KrmsAttributeDefinitionService.class, "getAttributeDefinitionService");
            getMethod.body().directStatement("if (attributeDefinitionService == null) {");
            getMethod.body().directStatement("    attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();");
            getMethod.body().directStatement("}");
            getMethod.body().directStatement("return attributeDefinitionService;");
        }

    }

    /*

                                         #######        ## ########
                                        ##     ##       ## ##     ##
                                        ##     ##       ## ##     ##
                                        ##     ##       ## ########
                                        ##     ## ##    ## ##     ##
                                        ##     ## ##    ## ##     ##
                                         #######   ######  ########

     */
    public static class QueryResultsGenerator {
        private final String contractInterfaceName;
        private final String className;
        private final String dtoClassName;
        private final JCodeModel codeModel;
        private final boolean hasAttributes;
        private final Map<String, String> foreignKeyTypes = new HashMap<String, String>();

        public QueryResultsGenerator(String contractInterfaceName, String className, String dtoClassName,
                Map<String, String> foreignKeyTypes, boolean hasAttributes) {
            this.contractInterfaceName = contractInterfaceName;
            this.className = className;
            this.codeModel = new JCodeModel();
            this.dtoClassName = dtoClassName;
            this.hasAttributes = hasAttributes;
            this.foreignKeyTypes.putAll(foreignKeyTypes);
        }

        public String generate() throws Exception {
            Class<?> contractInterface = Class.forName(contractInterfaceName);
            Set<FieldModel> fields = determineFieldsSet(contractInterface);
            if (className.endsWith("AttributeBo")) {
                Set<FieldModel> baseFields = determineFieldsSet(Class.forName(
                        "org.kuali.rice.krms.impl.repository.BaseJavaAttributeBo"));
                baseFields.remove(new FieldModel("objectId", java.lang.String.class));
                baseFields.remove(new FieldModel("class", java.lang.Class.class));
                baseFields.remove(new FieldModel("extension", Class.forName(
                        "org.kuali.rice.krad.bo.PersistableBusinessObjectExtension")));
                fields.addAll(baseFields);
                // parent id field
                fields.add(new FieldModel(Util.toLowerCaseFirstLetter(className.substring(className.lastIndexOf(".") + 1, className.indexOf("AttributeBo"))) + "Id", java.lang.String.class));
            }

            StringBuilder stringBuilder = new StringBuilder("-------------------------");
            stringBuilder.append(className).append(".xml");
            stringBuilder.append("-------------------------\n\n");
            List<String> xmlLines = new LinkedList<String>();
            stringBuilder.append("<class-descriptor class=\"" + className + "\" table=\"\">\n");
            for (FieldModel field: fields) {
                if ("id".equals(field.fieldName)) {
                    xmlLines.add("\t<field-descriptor name=\"id\" column=\"_ID\" jdbc-type=\"VARCHAR\" primarykey=\"true\" autoincrement=\"true\" sequence-name=\"_S\"/>\n");
                } else if ("active".equals(field.fieldName)) {
                    xmlLines.add("\t<field-descriptor name=\"active\" column=\"ACTV\" jdbc-type=\"VARCHAR\"\n"
                            + "\t\t\tconversion=\"org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion\" />\n");
                } else if (Util.VERSION_NUMBER_FIELD.equals(field.fieldName)) {
                    xmlLines.add("\t<field-descriptor name=\"" + Util.VERSION_NUMBER_FIELD + "\" column=\"VER_NBR\" jdbc-type=\"BIGINT\" locking=\"true\" />\n");
                } else if ("attributes".equals(field.fieldName)) {
                    String baseName = className.substring(0, className.indexOf("Bo"));
                    xmlLines.add("\t<collection-descriptor name=\"attributeBos\" element-class-ref=\"" + baseName + "AttributeBo\" \n"
                            + "\t\tcollection-class=\"org.apache.ojb.broker.util.collections.ManageableHashSet\" \n"
                            + "\t\tauto-retrieve=\"true\" auto-update=\"object\" auto-delete=\"object\" proxy=\"false\">\n"
                            + "\t\t\t<inverse-foreignkey field-ref=\"" + Util.toLowerCaseFirstLetter(className.substring(className.lastIndexOf(".") + 1, className.indexOf("Bo"))) + "Id\" />\n"
                            + "\t</collection-descriptor>\n");
                } else if ("attributeDefinition".equals(field.fieldName)) {
                    String baseName = className.substring(0, className.indexOf("Bo"));
                    xmlLines.add("\t<reference-descriptor name=\"attributeDefinition\" class-ref=\"org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo\" \n"
                            + "\t\tauto-retrieve=\"true\" auto-update=\"none\" auto-delete=\"none\" proxy=\"false\">\n"
                            + "\t\t\t<foreignkey field-ref=\"attributeDefinitionId\" />\n"
                            + "\t</reference-descriptor>\n");
                } else if ("attributeDefinitionId".equals(field.fieldName)) {
                    String baseName = className.substring(0, className.indexOf("Bo"));
                    xmlLines.add("\t<field-descriptor name=\"attributeDefinitionId\" column=\"ATTR_DEFN_ID\" jdbc-type=\"VARCHAR\" />\n");
                } else if ("typeId".equals(field.fieldName)) {
                    String baseName = className.substring(0, className.indexOf("Bo"));
                    xmlLines.add("\t<field-descriptor name=\"typeId\" column=\"TYP_ID\" jdbc-type=\"VARCHAR\" />\n");
                } else if (field.fieldName.endsWith("Id")) {
                    xmlLines.add("\t<field-descriptor name=\"" + field.fieldName + "\" column=\"_ID\" jdbc-type=\"VARCHAR\" />\n");
                    if (foreignKeyTypes.keySet().contains(field.fieldName)) {
                        String baseRefName = field.fieldName.substring(0, field.fieldName.indexOf("Id"));
                        xmlLines.add("\t<reference-descriptor name=\"" + baseRefName + "\" class-ref=\"org.kuali.rice.krms.impl.repository." + Util.toUpperCaseFirstLetter(baseRefName) + "Bo\" auto-retrieve=\"true\" auto-update=\"none\" auto-delete=\"none\" proxy=\"false\">\n"
                                + "\t\t<foreignkey field-ref=\"" + field.fieldName + "\" />\n"
                                + "\t</reference-descriptor>\n");
                    }
                } else {
                    xmlLines.add("\t<field-descriptor name=\"" + field.fieldName + "\" column=\"" + determineColumnName(field.fieldName) + "\" jdbc-type=\"VARCHAR\" />\n"); // TODO type lookup
                }
            }

            Collections.sort(xmlLines, new Comparator<String>(){
                String[] tagSortOrder = {"<field-descriptor", "<collection-descriptor", "<reference-descriptor"};
                /** Order by tagSortOrder, then the length of the name field, then alphabetical */
                @Override
                public int compare(String string1, String string2) {
                    String tag1 = string1.trim();
                    tag1 = tag1.substring(0, tag1.indexOf(" "));
                    String tag2 = string2.trim();
                    tag2 = tag2.substring(0, tag2.indexOf(" "));
                    if (tag1.equals(tag2)) { // equal tags sort by length next, then alphabetical
                        String name1 = string1.substring(string1.indexOf("name=\"") + 6, string1.length());
                        name1 = name1.substring(0, name1.indexOf("\""));
                        String name2 = string2.substring(string2.indexOf("name=\"") + 6, string2.length());
                        name2 = name2.substring(0, name2.indexOf("\""));
                        if (name1.length() == name2.length()) { // if the lengths are also the same, then alphabetical
                            return string1.trim().compareTo(string2.trim());
                        }
                        return name1.length() - name2.length();
                    } else { // The tags are not equal, sort by tagSortOrder
                        if (tagSortOrder[0].equals(tag1)) { // tag2 must be [1] or [2]
                            return -1;
                        } else if (tagSortOrder[2].equals(tag1)) { // tag2 must be [0] or [1]
                            return 1;
                        } else if (tagSortOrder[1].equals(tag1) && tagSortOrder[0].equals(tag2)) {
                            return 1;
                        } else return -1; // all that is left is tagSortOrder[1].equals(tag1) && tagSortOrder[2].equals(tag2)
                    }
                }
            });
            for(String line: xmlLines) {
                stringBuilder.append(line);
            }
            
            stringBuilder.append("</class-descriptor>\n");
            System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        }

        // TODO probably also need classname, as value is ATTR_VAL when an attribute but VAL when not
        String determineColumnName(String fieldName) {
            if ("name".equals(fieldName)) return "NM";
            if ("label".equals(fieldName)) return "LBL";
            if ("title".equals(fieldName)) return "TTL";
            if ("value".equals(fieldName)) return "ATTR_VAL";
            if ("typeId".equals(fieldName)) return "TYP_ID"; // hard coded above, but here for completeness
            if ("channel".equals(fieldName)) return "CHNL";
            if ("namespace".equals(fieldName)) return "NMSPC_CD";
            if ("description".equals(fieldName)) return "DESC_TXT";
            if ("versionNumber".equals(fieldName)) return "VER_NBR"; // hard coded above, but here for completeness
            if ("sequenceNumber".equals(fieldName)) return "SEQ_NO";
            if ("attributeDefinitionId".equals(fieldName)) return "ATTR_DEFN_ID";
            return "unable to determine column name for " + fieldName;
        }
    }

    /*

 #######  ##     ## ######## ########  ##    ##      ########  ########  ######  ##     ## ##       ########  ######
##     ## ##     ## ##       ##     ##  ##  ##       ##     ## ##       ##    ## ##     ## ##          ##    ##    ##
##     ## ##     ## ##       ##     ##   ####        ##     ## ##       ##       ##     ## ##          ##    ##
##     ## ##     ## ######   ########     ##         ########  ######    ######  ##     ## ##          ##     ######
##  ## ## ##     ## ##       ##   ##      ##         ##   ##   ##             ## ##     ## ##          ##          ##
##    ##  ##     ## ##       ##    ##     ##         ##    ##  ##       ##    ## ##     ## ##          ##    ##    ##
 ##### ##  #######  ######## ##     ##    ##         ##     ## ########  ######   #######  ########    ##     ######

     */
// To much work doing it this way, just search and replace in an existing one.
    public static class QueryResultsGen {

        private final String className;
        private final JCodeModel codeModel;

        public QueryResultsGen(String className) {
            this.className = className;
            this.codeModel = new JCodeModel();
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
            classModel._extends(AbstractDataTransferObject.class);
            classModel._implements(narrow(QueryResults.class, Class.forName(className), codeModel));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

    }

    /*
                     ######  ######## ########  ##     ## ####  ######  ########
                    ##    ## ##       ##     ## ##     ##  ##  ##    ## ##
                    ##       ##       ##     ## ##     ##  ##  ##       ##
                     ######  ######   ########  ##     ##  ##  ##       ######
                          ## ##       ##   ##    ##   ##   ##  ##       ##
                    ##    ## ##       ##    ##    ## ##    ##  ##    ## ##
                     ######  ######## ##     ##    ###    ####  ######  ########
     */
    public static class ServiceGenerator {
        private final String className;
        private final String dtoClassName;
        private final JCodeModel codeModel;
        private final String contractInterfaceName;
        private final Map<String, List<String>> enumsValues;
        private final List<Method> methods;

        public ServiceGenerator(String className, String dtoClassName, String contractInterfaceName, Map<String, List<String>> enumsValues, List<Method> methods) {
            this.className = className;
            this.dtoClassName = dtoClassName;
            this.codeModel = new JCodeModel();
            this.contractInterfaceName = contractInterfaceName;
            this.enumsValues = new HashMap<String, List<String>>(enumsValues);
            this.methods = new LinkedList<Method>(methods);
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC, className, ClassType.INTERFACE);

            Class<?> contractInterface = Class.forName(contractInterfaceName);
            List<FieldModel> fields = determineFields(contractInterface);

            renderClassJavadoc(classModel);
            renderCreate(classModel, dtoClassName);
            renderRead  (classModel, dtoClassName);
            renderUpdate(classModel, dtoClassName);
            renderDelete(classModel, dtoClassName);
            renderFinds(classModel, fields, Class.forName(dtoClassName), dtoClassName, methods);

            String renderName = null;
            if (className.endsWith("KrmsTypeBoService")) {
                renderName = className.substring(0, className.lastIndexOf("Service")).replace(".type", "");
            } else if (className.endsWith("ReferenceObjectBindingBoService")) {
                renderName = className.substring(0, className.lastIndexOf("Service")).replace(".reference.", ".");
            } else if (className.endsWith("TypeTypeRelationBoService")) {
                renderName = className.substring(0, className.lastIndexOf("Service")).replace(".typerelation.", ".");
            } else {
                renderName = className.substring(0, className.lastIndexOf("Service"));
            }
            renderTo(classModel, Class.forName(renderName), Class.forName(dtoClassName));
            renderFrom(classModel, Class.forName(renderName), Class.forName(dtoClassName));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        void renderClassJavadoc(JDefinedClass classModel) {
            JDocComment javadoc = classModel.javadoc();
            javadoc.append(Util.generateServiceJavadoc(classModel.name()));
        }

        void renderCreate(JDefinedClass classModel, String dtoClassName) throws Exception {
            String shortClassName = Util.determineShortName(dtoClassName);
            String lowerCaseClassName = Util.toLowerCaseFirstLetter(shortClassName);
            JMethod method = createMethodWithParam(classModel, Class.forName(dtoClassName), Class.forName(dtoClassName),  "create" + shortClassName);
            JDocComment javadoc = method.javadoc();
            javadoc.append(Util.generateCreateJavadoc(dtoClassName, shortClassName, lowerCaseClassName));
        }

        /**
         * Get
         */
        void renderRead(JDefinedClass classModel, String dtoClassName) throws Exception {
            String shortClassName = Util.determineShortName(dtoClassName);
            JMethod method = createMethodWithStringParam(classModel, Class.forName(dtoClassName), Util.toLowerCaseFirstLetter(shortClassName + "Id"), "get" + shortClassName);
            JDocComment javadoc = method.javadoc();
            javadoc.append(Util.generateReadJavadoc(dtoClassName));
        }

        void renderUpdate(JDefinedClass classModel, String dtoClassName) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            String dtoLowerCaseClassName = Util.toLowerCaseFirstLetter(dtoShortClassName);
            JMethod method = createMethodWithParam(classModel, codeModel.VOID, Class.forName(dtoClassName), "update" + dtoShortClassName);
            JDocComment javadoc = method.javadoc();
            javadoc.append(Util.generateUpdateJavadoc(dtoClassName, dtoShortClassName, dtoLowerCaseClassName));
        }

        void renderDelete(JDefinedClass classModel, String dtoClassName) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            JMethod method = createMethodWithStringParam(classModel, codeModel.VOID, dtoShortClassName + "Id", "delete" + dtoShortClassName);
            JDocComment javadoc = method.javadoc();
            javadoc.append(Util.generateDeleteJavadoc(dtoClassName, dtoShortClassName));
        }

        // TODO generate the other finder, the one implemented in the ServiceImpl below
        // TODO generate finder javadocs
        void renderFinds(JDefinedClass classModel, List<FieldModel> fields,  Class dtoClass, String dtoClassName, List<Method> methods) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            JClass returnClass = narrowedList(dtoClass, codeModel);
            if (Util.USE_SET) {
                returnClass = narrowedSet(dtoClass, codeModel);
            }

            // TODO aggregated finds ie ByTypeAndContext, etc.
            for (FieldModel field : fields) {
                if ((field.fieldName).endsWith("Id") && !field.fieldName.startsWith("first")) { // Don't create for Agenda's firstItemId for example
                    String by = Util.toUpperCaseFirstLetter(field.fieldName.substring(0, field.fieldName.indexOf("Id")));
//                    JMethod method = createMethodWithStringParam(classModel, returnClass, field.fieldName, "find" + dtoShortClassName + "sBy" + by);
                    JMethod method = createMethodWithStringParam(classModel, returnClass, field.fieldName, "find" + dtoShortClassName + "sBy" + by);
                } else if (enumsValues.containsKey(Util.toUpperCaseFirstLetter(field.fieldName))) {
                    JMethod method = createMethodWithParam(classModel, returnClass, field.fieldType, "find" + dtoShortClassName + "sBy" + Util.toUpperCaseFirstLetter(field.fieldName));
                } else if (!Util.UNVALIDATED_FIELD_SETS.contains(field.fieldName) && !"id".equals(field.fieldName)) {
                    JMethod method = createMethodWithParam(classModel, returnClass, field.fieldType, field.fieldName, "find" + dtoShortClassName + "sBy" + Util.toUpperCaseFirstLetter(field.fieldName));
                }
            }

            String findMethod = "find" + Util.determineShortName(dtoClassName) + "Ids";
            for (Method method : methods) {
                if (findMethod.equals(method.getName())) {
                    JMethod newmethod = classModel.method(JMod.PUBLIC, narrowedList(String.class, codeModel), findMethod);
                    newmethod.param(JMod.FINAL, QueryByCriteria.class, "queryByCriteria");

                    JMethod find = classModel.method(JMod.PUBLIC, Class.forName(dtoClassName + "QueryResults"), "find" + dtoShortClassName + "s");
                    find.param(JMod.FINAL, QueryByCriteria.class, "queryByCriteria");
                }
            }
        }

        void renderTo(JDefinedClass classModel, Class boClass, Class dtoClass) {
            JMethod method = createMethodWithParam(classModel, dtoClass, boClass, "to");
            method.javadoc().append(Util.generateToJavadoc(boClass, dtoClass));
        }

        void renderFrom(JDefinedClass classModel, Class boClass, Class dtoClass) {
            JMethod method = createMethodWithParam(classModel, boClass, dtoClass, "from");
            method.javadoc().append(Util.generateFromJavadoc(boClass, dtoClass));
        }
    }

    /*
             ######  ######## ########  ##     ## ####  ######  ########        #### ##     ## ########  ##
            ##    ## ##       ##     ## ##     ##  ##  ##    ## ##               ##  ###   ### ##     ## ##
            ##       ##       ##     ## ##     ##  ##  ##       ##               ##  #### #### ##     ## ##
             ######  ######   ########  ##     ##  ##  ##       ######           ##  ## ### ## ########  ##
                  ## ##       ##   ##    ##   ##   ##  ##       ##               ##  ##     ## ##        ##
            ##    ## ##       ##    ##    ## ##    ##  ##    ## ##               ##  ##     ## ##        ##
             ######  ######## ##     ##    ###    ####  ######  ########        #### ##     ## ##        ########
     */
    public static class ServiceImplGenerator {
        private final String className;
        private final String dtoClassName;
        private final String boClassName;
        private final JCodeModel codeModel;
        private final boolean hasAttributes;
        private final boolean hasFinder;
        private final List<FieldModel> fields;
        private final List<Method> serviceMethods;
        private final Class serviceInterface;


        public ServiceImplGenerator(String className, Class serviceInterface, String boClassName, String dtoClassName, List<FieldModel> fields, List<Method> serviceMethods, boolean hasAttributes, boolean hasFinder) {
            this.className = className;
            this.serviceInterface = serviceInterface;
            this.dtoClassName = dtoClassName;
            if (className.endsWith("KrmsTypeRepositoryServiceImpl")) {
                this.boClassName = boClassName.replace(".type.", "."); // TODO
            } else if (className.endsWith("TypeTypeRelationBoServiceImpl")) {
                this.boClassName = boClassName.replace(".typerelation.", "."); // TODO
            } else {
                this.boClassName = boClassName.replace(".reference.", "."); // TODO
            }
            this.codeModel = new JCodeModel();
            this.hasAttributes = hasAttributes;
            this.hasFinder = hasFinder;
            this.fields = new LinkedList<FieldModel>(fields);
            this.serviceMethods = new LinkedList<Method>(serviceMethods);
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
            classModel._implements(serviceInterface);

            String baseClassName = null;
            if (className.endsWith("KrmsTypeRepositoryServiceImpl")) {
                baseClassName = "KrmsType";
            } else {
                baseClassName = Util.determineShortName(className.substring(0, className.indexOf("Bo")));
            }

            renderClassJavadoc(classModel);
            renderFields(classModel);
            renderFieldsSetters(classModel);
            renderKrmsAttributeDefinitionService(classModel);

            renderCreate(classModel, dtoClassName);
            renderRead  (classModel, dtoClassName);
            renderUpdate(classModel, dtoClassName, baseClassName);
            renderDelete(classModel, dtoClassName);
            renderFieldFinds(classModel, fields, Class.forName(dtoClassName), dtoClassName);
            renderFinds(classModel, fields, Class.forName(dtoClassName), dtoClassName, serviceMethods);
            renderConvertBosToImmutables(classModel, Class.forName(dtoClassName), dtoClassName, Class.forName(boClassName));
            renderTo(classModel, Class.forName(boClassName), Class.forName(dtoClassName));
            renderFrom(classModel, fields, Class.forName(boClassName), Class.forName(dtoClassName), baseClassName);
            
            if (hasAttributes) {
                String attributeBoClassName = boClassName.toString().replace("Bo", "AttributeBo");
//                String contractImplClassName = boClassName.toString().replace("Bo", "").replace(".impl.", ".api.");
                renderBuildAttributeCollection(classModel, baseClassName, Class.forName(attributeBoClassName), Class.forName(dtoClassName), className, codeModel, JMod.PRIVATE);
                renderBuildAttributeBoSet(classModel, Class.forName(attributeBoClassName), Class.forName(dtoClassName), codeModel, JMod.PRIVATE);
                renderBuildAttributeBoList(classModel, Class.forName(attributeBoClassName), Class.forName(dtoClassName), codeModel, JMod.PRIVATE);
            }
            
            renderIncomingParamCheck(classModel);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        private void renderFrom(JDefinedClass classModel, List<FieldModel> fields, Class<?> boClass, Class<?> dtoClass,
                String baseClassName) {
            JMethod method = classModel.method(JMod.PUBLIC, boClass, "from");
            method.param(dtoClass, Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())));

            JBlock methodBody = method.body();
            methodBody.directStatement("return " + Util.determineShortName(boClass.getSimpleName()) + ".from(" + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClass.getSimpleName())) + ");");
            method.annotate(Override.class);
        }

        void renderClassJavadoc(JDefinedClass classModel) {
            JDocComment javadoc = classModel.javadoc();
            javadoc.append(Util.generateServiceImplJavadoc(classModel.name()));
        }

        private void renderFields(JDefinedClass classModel) {
            JFieldVar field = classModel.field(JMod.PRIVATE, org.kuali.rice.krad.service.BusinessObjectService.class,
                    "businessObjectService");
            JFieldVar ads = classModel.field(JMod.PRIVATE, KrmsAttributeDefinitionService.class, "attributeDefinitionService");
            if (hasFinder) {
                JFieldVar search = classModel.field(JMod.PRIVATE, CriteriaLookupService.class, "criteriaLookupService");
            }
        }

        void renderFieldsSetters(JDefinedClass classModel) {
            JMethod method = createMethodWithParam(classModel, codeModel.VOID, org.kuali.rice.krad.service.BusinessObjectService.class, "setBusinessObjectService");
            JBlock methodBody = method.body();
            methodBody.directStatement("this.businessObjectService = businessObjectService;");
            method.javadoc().append(Util.generateSetterJavadoc("BusinessObjectService"));
            if (hasFinder) {
                JMethod critera = createMethodWithParam(classModel, codeModel.VOID, CriteriaLookupService.class, "setCriteriaLookupService");
                critera.body().directStatement("this.criteriaLookupService = criteriaLookupService;");
            }
        }

        void renderCreate(JDefinedClass classModel, String dtoClassName) throws Exception {
            String shortClassName = Util.determineShortName(dtoClassName);
            String lowerCaseClassName = Util.toLowerCaseFirstLetter(shortClassName);
            JMethod method = createMethodWithParam(classModel, Class.forName(dtoClassName), Class.forName(dtoClassName),  "create" + shortClassName);
            method.annotate(Override.class);
            JBlock methodBody = method.body();
            methodBody.directStatement("incomingParamCheck(" + lowerCaseClassName + " , \"" + lowerCaseClassName + "\");");
            methodBody.directStatement("final String " + lowerCaseClassName + "IdKey = " + lowerCaseClassName + ".getId();");
            methodBody.directStatement("final " + shortClassName + " existing = get" + shortClassName + "(" +  lowerCaseClassName + "IdKey);");
            methodBody.directStatement("if (existing != null){ throw new IllegalStateException(\"the " + shortClassName + " to create already exists: \" + " + lowerCaseClassName + ");\t}");
            methodBody.directStatement(shortClassName + "Bo bo = (" + shortClassName + "Bo)businessObjectService.save(from(" + lowerCaseClassName + "));");
            methodBody.directStatement("return " + shortClassName + "Bo.to(bo);");
        }

        /**
         * Get
         */
        void renderRead(JDefinedClass classModel, String dtoClassName) throws Exception {
            String shortClassName = Util.determineShortName(dtoClassName);
            String lowerCaseClassName = Util.toLowerCaseFirstLetter(shortClassName);
            JMethod method = createMethodWithStringParam(classModel, Class.forName(dtoClassName), lowerCaseClassName + "Id", "get" + shortClassName);
            method.annotate(Override.class);
            JBlock methodBody = method.body();
            methodBody.directStatement("incomingParamCheck(" + lowerCaseClassName + "Id , \"" + lowerCaseClassName + "Id\");");
            methodBody.directStatement(shortClassName + "Bo bo = businessObjectService.findBySinglePrimaryKey(" + shortClassName + "Bo.class, " + lowerCaseClassName + "Id);");
            methodBody.directStatement("return " + shortClassName + "Bo.to(bo);");
        }

        void renderUpdate(JDefinedClass classModel, String dtoClassName, String baseClassName) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            String dtoLowerCaseClassName = Util.toLowerCaseFirstLetter(dtoShortClassName);
            JMethod method = createMethodWithParam(classModel, codeModel.VOID, Class.forName(dtoClassName), "update" + dtoShortClassName);
            method.annotate(Override.class);
            JBlock methodBody = method.body();
            methodBody.directStatement("incomingParamCheck(" + dtoLowerCaseClassName + " , \"" + dtoLowerCaseClassName + "\");");
            methodBody.directStatement("final " + dtoShortClassName + " existing = get" + dtoShortClassName + "(" +  dtoLowerCaseClassName + ".getId());");
            methodBody.directStatement("if (existing == null){ throw new IllegalStateException(\"the " + dtoShortClassName + " to update does not exists: \" + " + dtoLowerCaseClassName + ");}");
            if (Util.IS_KRMS) {
                methodBody.directStatement("final " + dtoShortClassName + " toUpdate;");
            } else {
                methodBody.directStatement("final " + dtoShortClassName + " toUpdate;");
            }
            methodBody.directStatement("if (!existing.getId().equals(" + dtoLowerCaseClassName + ".getId())){");
            methodBody.directStatement("    // if passed in id does not match existing id, correct it");
            if (Util.IS_KRMS) {
                methodBody.directStatement("    final " + dtoShortClassName + "Definition.Builder builder = " + dtoShortClassName + "Definition.Builder.create(" + dtoLowerCaseClassName + ");");
            } else {
                methodBody.directStatement("    final " + dtoShortClassName + ".Builder builder = " + dtoShortClassName + ".Builder.create(" + dtoLowerCaseClassName + ");");
            }
            methodBody.directStatement("    builder.setId(existing.getId());");
            methodBody.directStatement("    toUpdate = builder.build();");
            methodBody.directStatement("} else {");
            methodBody.directStatement("    toUpdate = " + dtoLowerCaseClassName + ";");
            methodBody.directStatement("}\n");

            methodBody.directStatement("// copy all updateable fields to bo");
            methodBody.directStatement(dtoShortClassName + "Bo boToUpdate = from(toUpdate);\n");

            if (hasAttributes) {
                methodBody.directStatement("// delete any old, existing attributes");
                methodBody.directStatement("Map<String,String> fields = new HashMap<String,String>(1);");
 //               String baseClassName = Util.determineShortName(className.substring(0, className.indexOf("Bo")));
                methodBody.directStatement("fields.put(KrmsImplConstants.PropertyNames." + baseClassName + "." + Util.toConstantsVariable(baseClassName)
                        + "_ID, toUpdate.getId()); // TODO verify PropertyNames." + baseClassName + "." + Util.toConstantsVariable(baseClassName) + "_ID");
                methodBody.directStatement("businessObjectService.deleteMatching(" + baseClassName + "AttributeBo.class, fields);\n");
            }

            methodBody.directStatement("// update the rule and create new attributes");
            methodBody.directStatement(" businessObjectService.save(boToUpdate);");
        }

        void renderDelete(JDefinedClass classModel, String dtoClassName) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            String lowerCaseClassName = Util.toLowerCaseFirstLetter(dtoShortClassName);
            JMethod method = createMethodWithStringParam(classModel, codeModel.VOID, dtoShortClassName + "Id", "delete" + dtoShortClassName);
            method.annotate(Override.class);
            JBlock methodBody = method.body();
            methodBody.directStatement("incomingParamCheck(" + lowerCaseClassName + "Id , \"" + lowerCaseClassName + "Id\");");
            methodBody.directStatement("final " + dtoShortClassName + " existing = get" + dtoShortClassName + "(" +  lowerCaseClassName + "Id);");
            methodBody.directStatement("if (existing == null){ throw new IllegalStateException(\"the " + dtoShortClassName + " to delete does not exists: \" + " + lowerCaseClassName + "Id);}");
            methodBody.directStatement("businessObjectService.delete(from(existing));");
        }

        void renderConvertBosToImmutables(JDefinedClass classModel,  Class dtoClass, String dtoClassName, Class boClass) throws Exception {
            JClass returnClass = narrowedList(dtoClass, codeModel);
            if (Util.USE_SET) {
                returnClass = narrowedSet(dtoClass, codeModel);
            }
            JClass paramClass = narrow(boClass, Collection.class, codeModel);

            JMethod method = classModel.method(JMod.PUBLIC, returnClass, "convertBosToImmutables");
            method.param(JMod.FINAL, paramClass, Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + "s");
            if (Util.USE_SET) {
                method.body().directStatement("Set<" + dtoClass.getSimpleName() + "> immutables = new HashSet<" + dtoClass.getSimpleName() + ">();");
            } else {
                method.body().directStatement("List<" + dtoClass.getSimpleName() + "> immutables = new LinkedList<" + dtoClass.getSimpleName() + ">();");
            }
            method.body().directStatement("if (" + Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + "s != null) {");
            method.body().directStatement("    " + dtoClass.getSimpleName() + " immutable = null;");
            method.body().directStatement("    for (" + boClass.getSimpleName() +" bo : " + Util.toLowerCaseFirstLetter(boClass.getSimpleName()) + "s ) {");
            method.body().directStatement("        immutable = to(bo);");
            method.body().directStatement("        immutables.add(immutable);");
            method.body().directStatement("    }");
            method.body().directStatement("}");
            if (Util.USE_SET) {
                method.body().directStatement("return Collections.unmodifiableSet(immutables);");
            }
            method.body().directStatement("return Collections.unmodifiableList(immutables);");
        }

        void renderFieldFinds(JDefinedClass classModel, List<FieldModel> fields, Class dtoClass, String dtoClassName) throws Exception {
            String dtoShortClassName = Util.determineShortName(dtoClassName);
            String lowerCaseClassName = Util.toLowerCaseFirstLetter(dtoShortClassName);

            JClass returnClass = narrowedList(dtoClass, codeModel);
            if (Util.USE_SET) {
                returnClass = narrowedSet(dtoClass, codeModel);
            }

            // TODO aggregated finds ie ByTypeAndContext, etc.
            for (FieldModel field : fields) {
                if ((field.fieldName).endsWith("Id") && !field.fieldName.startsWith("first")) { // Don't create for Agenda's firstItemId for example
                    String by = Util.toUpperCaseFirstLetter(field.fieldName.substring(0, field.fieldName.indexOf("Id")));
                    createFindByMethod(classModel, dtoShortClassName, returnClass, field, by);
                } else if (!Util.UNVALIDATED_FIELD_SETS.contains(field.fieldName) && !"id".equals(field.fieldName)) {
                    createFindByMethod(classModel, dtoShortClassName, returnClass, field, Util.toUpperCaseFirstLetter(field.fieldName));
                }
            }
//            JMethod method = createMethodWithStringParam(classModel, dtoClass, lowerCaseClassName + "Id", "get" + dtoShortClassName);
//            renderStringUtilsIsBlankIllegalArgumentExceptionGuard(lowerCaseClassName + "Id", method);
//            method.body().directStatement(dtoShortClassName + "Bo bo = businessObjectService.findBySinglePrimaryKey(" + dtoShortClassName + "Bo.class, " + lowerCaseClassName + "Id)");
//            method.body().directStatement("return to(bo);");
//            method.annotate(Override.class);
        }

        void renderFinds(JDefinedClass classModel, List<FieldModel> fields, Class dtoClass, String dtoClassName, List<Method> methods) throws Exception {
            String dtoShortClassName = Util.shortClassName(dtoClassName);
            String shortBoClassName = Util.determineShortName(boClassName);
//            String lowerCaseClassName = Util.toLowerCaseFirstLetter(dtoShortClassName);

//            JClass returnSet = narrowedSet(String.class, codeModel);
            String findMethod = "find" + Util.determineShortName(dtoClassName) + "Ids";
            
            for (Method method : methods) {
                if (findMethod.equals(method.getName())) {
                    JMethod newmethod = classModel.method(JMod.PUBLIC, narrowedList(String.class, codeModel), findMethod);
                    newmethod.param(JMod.FINAL, QueryByCriteria.class, "queryByCriteria");
                    newmethod.body().directStatement("incomingParamCheck(queryByCriteria, \"queryByCriteria\");");
                    newmethod.body().directStatement(dtoShortClassName + "QueryResults results = find" + dtoShortClassName + "s(queryByCriteria);");
                    newmethod.body().directStatement("List<String> result = new ArrayList<String>();");
                    newmethod.body().directStatement("for (" + dtoShortClassName + " " + Util.toLowerCaseFirstLetter(dtoShortClassName) + ": results.getResults()) {");
                    newmethod.body().directStatement("    result.add(" + Util.toLowerCaseFirstLetter(dtoShortClassName) + ".getId());");
                    newmethod.body().directStatement("}");
                    newmethod.body().directStatement("return Collections.unmodifiableList(result);");
                    newmethod.annotate(Override.class);

                    JMethod find = classModel.method(JMod.PUBLIC, Class.forName(dtoClassName + "QueryResults"), "find" + dtoShortClassName + "s");
                    find.param(JMod.FINAL, QueryByCriteria.class, "queryByCriteria");
                    find.body().directStatement("LookupCustomizer.Builder<" + shortBoClassName + "> lc = LookupCustomizer.Builder.create();");
                    find.body().directStatement("lc.setPredicateTransform(AttributeTransform.getInstance());");
                    find.body().directStatement("GenericQueryResults<" + shortBoClassName + "> results = criteriaLookupService.lookup(" + shortBoClassName + ".class, queryByCriteria, lc.build());");
                    find.body().directStatement(dtoShortClassName + "QueryResults.Builder builder = " + dtoShortClassName + "QueryResults.Builder.create();");
                    find.body().directStatement("builder.setMoreResultsAvailable(results.isMoreResultsAvailable());");
                    find.body().directStatement("builder.setTotalRowCount(results.getTotalRowCount());");
                    find.body().directStatement("final List<" + dtoShortClassName + ".Builder> ims = new ArrayList<" + dtoShortClassName + ".Builder>();");
                    find.body().directStatement("for (" + shortBoClassName + " bo : results.getResults()) {");
                    find.body().directStatement("    ims.add(" + dtoShortClassName + ".Builder.create(bo));");
                    find.body().directStatement("}");
                    find.body().directStatement("builder.setResults(ims);");
                    find.body().directStatement("return builder.build();");
                    find.annotate(Override.class);
                }
            }
        }
        
        private void createFindByMethod(JDefinedClass classModel, String dtoShortClassName, JClass returnList,
                FieldModel field, String by) {
            JMethod method = createMethodWithParam(classModel, returnList, field.fieldType, field.fieldName, "find" + dtoShortClassName + "sBy" + by);
            if ("String".equals(field.fieldType.getSimpleName())) {
                renderStringUtilsIsBlankIllegalArgumentExceptionGuard(field.fieldName, method);
            } else {
                renderIsNullIllegalArgumentExceptionGuard(field.fieldName, method)   ;
            }
            method.body().directStatement("final Map<String, Object> map = new HashMap<String, Object>();");
            method.body().directStatement("map.put(\"" + field.fieldName + "\", " + field.fieldName + ");");
            if (Util.USE_SET ) {
                method.body().directStatement("Set<" + dtoShortClassName + "Bo> bos = (Set<" + dtoShortClassName + "Bo>) businessObjectService.findMatching(" + dtoShortClassName + "Bo.class, map);");
            } else {
                method.body().directStatement("List<" + dtoShortClassName + "Bo> bos = (List<" + dtoShortClassName + "Bo>) businessObjectService.findMatching(" + dtoShortClassName + "Bo.class, map);");
            }
            method.body().directStatement("return convertBosToImmutables(bos);");
            method.annotate(Override.class);
        }

        void renderTo(JDefinedClass classModel, Class boClass, Class dtoClass) {
            JMethod method = createMethodWithParam(classModel, dtoClass, boClass, "to");
            method.annotate(Override.class);
            JBlock methodBody = method.body();
            methodBody.directStatement("return " + boClass.getSimpleName()
                    + ".to(" + Util.toLowerCaseFirstLetter(Util.determineShortName(boClass.getSimpleName())) + ");");
        }


        void renderKrmsAttributeDefinitionService(JDefinedClass classModel) {
            JMethod setMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "setAttributeDefinitionService");
            setMethod.param(KrmsAttributeDefinitionService.class, "attributeDefinitionService");
            setMethod.body().directStatement("this.attributeDefinitionService = attributeDefinitionService;");

            JMethod getMethod = classModel.method(JMod.PUBLIC, KrmsAttributeDefinitionService.class, "getAttributeDefinitionService");
            getMethod.body().directStatement("if (attributeDefinitionService == null) {");
            getMethod.body().directStatement("    attributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();");
            getMethod.body().directStatement("}");
            getMethod.body().directStatement("return attributeDefinitionService;");
        }

        void renderIncomingParamCheck(JDefinedClass classModel) {
            JMethod method = classModel.method(JMod.PRIVATE, codeModel.VOID, "incomingParamCheck");
            method.param(Object.class, "object");
            method.param(String.class, "name");
            method.body().directStatement("if (object == null) {");
            method.body().directStatement("    throw new IllegalArgumentException(name + \" was null\");");
            method.body().directStatement("} else if (object instanceof String");
            method.body().directStatement("        && StringUtils.isBlank((String)object)) {");
            method.body().directStatement("    throw new IllegalArgumentException(name + \" was blank\");");
            method.body().directStatement("}");
        }
    }

    /*
           ###    ######## ######## ########  #### ########  ##     ## ######## ########
          ## ##      ##       ##    ##     ##  ##  ##     ## ##     ##    ##    ##
         ##   ##     ##       ##    ##     ##  ##  ##     ## ##     ##    ##    ##
        ##     ##    ##       ##    ########   ##  ########  ##     ##    ##    ######
        #########    ##       ##    ##   ##    ##  ##     ## ##     ##    ##    ##
        ##     ##    ##       ##    ##    ##   ##  ##     ## ##     ##    ##    ##
        ##     ##    ##       ##    ##     ## #### ########   #######     ##    ########
     */
    public static class AttributeContractGenerator {
        private final String className;
        private final String parentId;
        private final JCodeModel codeModel;

        public AttributeContractGenerator(String className) {
            this.className = className;
            this.codeModel = new JCodeModel();
            String idName = Util.toLowerCaseFirstLetter(Util.shortClassName(className));
            if (idName.indexOf("Definition") > -1) {
                idName = idName.substring(0, idName.indexOf("Definition")) + "Id";
            }
            this.parentId = idName.substring(0, idName.indexOf("Attribute")) + "Id";
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC, className, ClassType.INTERFACE);
            classModel._extends(Class.forName("org.kuali.rice.krms.api.repository.BaseAttributeContract"));
//            classModel._extends(Class.forName("org.kuali.rice.core.api.mo.common.Identifiable")); // BaseAttirbuteContract extends Identifiable
            classModel._extends(Class.forName("org.kuali.rice.core.api.mo.common.Versioned"));

            String definition = classModel.name().substring(0, classModel.name().indexOf("Contract"));
            if (definition.indexOf("Definition") > -1) {
                definition = classModel.name().substring(0, classModel.name().indexOf("Definition"));
            }
            renderClassJavadoc(classModel, definition);
            renderFieldsGetters(classModel, definition);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();
        }

        void renderClassJavadoc(JDefinedClass classModel, String definition) {
            JDocComment javadoc = classModel.javadoc();
            javadoc.append("Defines the contract for a {@link " +definition + "}.\n"
                    + "@see " + definition + "\n\n"
                    + Util.CLASS_AUTHOR_JAVADOC);
        }

        void renderFieldsGetters(JDefinedClass classModel, String definition) {
            JMethod getMethod = classModel.method(JMod.PUBLIC, (Class)String.class, Util.generateGetterName(parentId, false));
            getMethod.javadoc().append(Util.generateGetterJavadoc(parentId, definition));
        }
    }
/*

 ######  ######## ########  ##     ## ####  ######  ########       #### ##     ## ########  ##          ######## ########  ######  ########
##    ## ##       ##     ## ##     ##  ##  ##    ## ##              ##  ###   ### ##     ## ##             ##    ##       ##    ##    ##
##       ##       ##     ## ##     ##  ##  ##       ##              ##  #### #### ##     ## ##             ##    ##       ##          ##
 ######  ######   ########  ##     ##  ##  ##       ######          ##  ## ### ## ########  ##             ##    ######    ######     ##
      ## ##       ##   ##    ##   ##   ##  ##       ##              ##  ##     ## ##        ##             ##    ##             ##    ##
##    ## ##       ##    ##    ## ##    ##  ##    ## ##              ##  ##     ## ##        ##             ##    ##       ##    ##    ##
 ######  ######## ##     ##    ###    ####  ######  ########       #### ##     ## ##        ########       ##    ########  ######     ##

 */
    public static class ServiceImplTestGenerator {
        private final String className;
        private final String dtoClassName;
        private final String boClassName;
        private final String shortBoClassName;
        private final JCodeModel codeModel;
        private final boolean hasAttributes;
        private final List<FieldModel> fields;
        private final String shortClassNameUnderTest;
        private final Class classUnderTest;
        private final Map<String, String> foreignKeyTypes = new HashMap<String, String>();

        public ServiceImplTestGenerator(String className, String boClassName, String dtoClassName, List<FieldModel> fields, Map<String, String> foreignKeyTypes, boolean hasAttributes) throws Exception {
            this.dtoClassName = dtoClassName;
            this.boClassName = boClassName;
            this.shortBoClassName = Util.determineShortName(boClassName);
            this.codeModel = new JCodeModel();
            this.hasAttributes = hasAttributes;
            this.fields = new LinkedList<FieldModel>(fields);
            if (className.endsWith("KrmsTypeRepositoryServiceImplGenTest")) {
                this.classUnderTest = Class.forName(className.replace("KrmsTypeRepository", "KrmsTypeBo").replace(".type.", ".").replace(".api.", ".impl.").replace("GenTest", ""));
                this.className = className.replace("KrmsTypeRepository", "KrmsTypeBo").replace(".api.", ".impl.").replace(".type.", ".");
            } else {
                this.classUnderTest = Class.forName(className.substring(0, className.indexOf("GenTest")));
                this.className = className;
            }
            this.foreignKeyTypes.putAll(foreignKeyTypes);
            this.shortClassNameUnderTest = classUnderTest.getSimpleName();
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
//            classModel._implements(KrmsAttributeDefinitionService.class);
            Class<?> serviceInterface = null;
            if (className.endsWith("KrmsTypeBoServiceImplGenTest")) {
                serviceInterface = Class.forName(className.replace(".KrmsTypeBoServiceImplGenTest", ".type.KrmsTypeRepositoryService").replace(".impl.", ".api."));
            } else {
                serviceInterface = Class.forName(className.substring(0, className.lastIndexOf("Impl")));
            }

            List<Method> byMethods = new LinkedList<Method>();
            Method[] methods = serviceInterface.getMethods();
            for (Method method : methods) {
                if (method.getName().indexOf("By") > -1) {
                    byMethods.add(method);
                }
            }
            renderClassAuthorJavadoc(classModel);
            renderCutField(classModel, classUnderTest, Class.forName(dtoClassName));
            renderDefinitionAndGetter(classModel, Class.forName(dtoClassName));
            renderReuseSetImpl(classModel, classUnderTest);
            renderMockBefore(classModel, classUnderTest);
            renderByFailureTests(classModel, codeModel, byMethods, classUnderTest);
            renderTests(classModel, codeModel, Arrays.asList(methods), fields, classUnderTest, shortBoClassName);
            renderCreate(classModel,Class.forName(dtoClassName), foreignKeyTypes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        void renderCreate(JDefinedClass classModel, Class definitionClass, Map<String, String> foreignKeyTypes) {
            JMethod method = classModel.method(JMod.NONE, codeModel.VOID, "create" + definitionClass.getSimpleName());
            // params like in DefGenTest.buildFullFK...
            method.body().directStatement("// TODO change the Object type of the parameters");
            // call foreign key type's Test.buildFull to satisfy dependencies.
            for (FieldModel field: fields) {
                if (foreignKeyTypes.keySet().contains(field.fieldName)) {
                    String n = field.fieldName;
                    if (field.fieldName.endsWith("Id")) {
                        n = n.substring(0, n.length()-2);
                    }
                    //                    final String s = Util.determineShortName(foreignKeyTypes.get(field.fieldName));
                    method.param(Object.class, n);
                }
            }
            method.body().directStatement(definitionClass.getSimpleName() + " def = " + definitionClass.getSimpleName() +
                    "GenTest.buildFullFK" + definitionClass.getSimpleName() + "(" + paramForeignKeyObjectsLine() + ");");
            method.body().directStatement(Util.toLowerCaseFirstLetter(definitionClass.getSimpleName()) + " = " + Util.toLowerCaseFirstLetter(definitionClass.getSimpleName()) + "BoServiceImpl.create" + definitionClass.getSimpleName() + "(def);");
        }

        // like paramForeignKeysAndConstantsLine without the constants
        String paramForeignKeyObjectsLine() {
            StringBuilder paramLine = new StringBuilder();
            String createParams = builderCreate(fields, null);
            String token = null;
            if (createParams.length() > 0) {
                StringTokenizer tokenizer = new StringTokenizer(createParams, ",");
                for (int i = 0, s = tokenizer.countTokens(); i < s; i++) {
                    token = tokenizer.nextToken().trim();
                    if (foreignKeyTypes.containsKey(token)) {
                        if (token.endsWith("Id")) {
                            token = token.substring(0, token.length() -  2);
                            paramLine.append(token + ", ");
                        }
                    }
                }
            }
            if (paramLine.toString().endsWith(", ")) {
                paramLine = new StringBuilder(paramLine.toString().substring(0, paramLine.toString().length() - 2));
            }
            return paramLine.toString();
        }

        void renderDefinitionAndGetter(JDefinedClass classModel, Class definitionClass) {
//            JFieldVar field = classModel.field(JMod.NONE, definitionClass, Util.toLowerCaseFirstLetter(Util.determineShortName(definitionClass.getSimpleName())));
            JMethod method = classModel.method(JMod.NONE, definitionClass, Util.generateGetterName(definitionClass.getSimpleName(), false));
            method.body().directStatement("return " + Util.toLowerCaseFirstLetter(Util.determineShortName(definitionClass.getSimpleName())) + ";");
        }


        void renderReuseSetImpl(JDefinedClass classModel, Class cutClass) throws Exception {
            JMethod set = classModel.method(JMod.PUBLIC, codeModel.VOID, "set" + cutClass.getSimpleName());
            set.param(cutClass, "impl");
            set.body().directStatement("this." + Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + " = impl;");

            JMethod create = classModel.method(JMod.PUBLIC | JMod.STATIC, Class.forName(className), "create");
            create.param(cutClass, "impl");
            create.body().directStatement(className + " test = new " + className + "();");
            create.body().directStatement("test.set" + cutClass.getSimpleName() + "(impl);");
            create.body().directStatement("return test;");
            
        }

        void renderMockBefore(JDefinedClass classModel, Class cutClass) {
            JMethod before = classModel.method(JMod.PUBLIC, codeModel.VOID, "setUp");
            before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + " = new " + cutClass.getSimpleName() + "();");
            if (hasAttributes) {
                before.body().directStatement("KrmsAttributeDefinitionService mockAttributeService = mock(KrmsAttributeDefinitionService.class);");
                before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + ".setAttributeDefinitionService(mockAttributeService);");
            }
            before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + ".setBusinessObjectService(mock(BusinessObjectService.class));// TODO Import static org.mockito.Mockito.*;");
            before.annotate(Before.class);
        }

        static void renderByFailureTests(JDefinedClass classModel, JCodeModel codeModel, List<Method> byMethods, Class classUnderTest) {
            for (Method method: byMethods) {
                JMethod nullMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_null_fail", IllegalArgumentException.class);
                nullMethod.body().directStatement(Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + "." + method.getName() + "(" + methodParamList(
                        method, "null") + ");");

                if ("String".equals(method.getReturnType().getSimpleName())) {
                    JMethod emptyMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_empty_fail", IllegalArgumentException.class);
                    emptyMethod.body().directStatement(Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + "." + method.getName() + "(" + methodParamList(
                            method, "\"\"") + ");");

                    JMethod whitespaceMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_whitespace_fail", IllegalArgumentException.class);
                    whitespaceMethod.body().directStatement(Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + "." + method.getName() + "(" + methodParamList(
                            method, "\"      \"") + ");");
                }
            }
        }

        static void renderTests(JDefinedClass classModel, JCodeModel codeModel, List<Method> methods, List<FieldModel> fields, Class classUnderTest, String shortBoClassName) {
            String shortClassNameUnderTest = classUnderTest.getSimpleName();
            String def = shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo"));
            if (Util.IS_KRMS) {
                def = def + "Definition";
            }

            for (Method method: methods) {
                if (method.getName().startsWith("create")) {
                    JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName());
                    JAnnotationUse annotation = testMethod.annotate(Test.class);

                    testMethod.body().directStatement(def + " def = " + shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo")) + "GenTest.buildFull" + def + "();");
                    testMethod.body().directStatement(Util.toLowerCaseFirstLetter(shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo"))) + " = " + Util.toLowerCaseFirstLetter(shortClassNameUnderTest) + "." + method.getName() + "(def);");
                }
                if (method.getName().startsWith("create") || method.getName().startsWith("delete") || method.getName().startsWith("update")) {
                    JMethod testNullMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_null_fail", IllegalArgumentException.class);
                    testNullMethod.body().directStatement(Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + "." + method.getName() + "(" + methodParamList(method, "null") + ");");
                } else if ("from".equals(method.getName())) {
                    renderFromTest(classModel, codeModel, fields, classUnderTest, shortBoClassName,
                            shortClassNameUnderTest, def);
                } else if ("to".equals(method.getName())) {
                    JMethod testFromMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_to");
                    JAnnotationUse annotationFrom = testFromMethod.annotate(Test.class);
                    testFromMethod.body().directStatement(def + " def = " + shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo")) + "GenTest.buildFull" + def + "();");
                    testFromMethod.body().directStatement(shortBoClassName + " " + Util.toLowerCaseFirstLetter(shortBoClassName) + " = " + Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + ".from(def);");
                    testFromMethod.body().directStatement(def + " def2 = " + shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo") + 2) + ".to(" + Util.toLowerCaseFirstLetter(shortBoClassName) + ");");
                    testFromMethod.body().directStatement("assert(def.equals(def2));");
                }
            }
        }

    // TODO move to the bo test
        private static void renderFromTest(JDefinedClass classModel, JCodeModel codeModel, List<FieldModel> fields,
                Class classUnderTest, String shortBoClassName, String shortClassNameUnderTest, String def) {JMethod
                testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_from_null_yields_null");
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            testMethod.body().directStatement("assert(" + Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + ".from(null) == null);");

            JMethod testFromMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_from");
            JAnnotationUse annotationFrom = testFromMethod.annotate(Test.class);
            testFromMethod.body().directStatement(def + " def = " + shortClassNameUnderTest.substring(0, shortClassNameUnderTest.indexOf("Bo")) + "GenTest.buildFull" + def + "();");
            testFromMethod.body().directStatement(shortBoClassName + " " + Util.toLowerCaseFirstLetter(shortBoClassName) + " = " + Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName()) + ".from(def);");
            for (FieldModel field: fields) {
                if (Util.VALIDATED_FIELDS_NOT_BLANK.contains(field.fieldName)) { // other fields will be null
                    if ("String".equals(field.fieldType.getSimpleName())) {
                        testFromMethod.body().directStatement("assert(" + Util.toLowerCaseFirstLetter(shortBoClassName) + "." + Util.generateGetterName(field.fieldName, false) + "().equals(def." + Util.generateGetterName(field.fieldName, false) + "()));");
                    }
                }
            }
        }

        private static String methodParamList(Method method, String paramValue) {
                StringBuilder stringBuilder = new StringBuilder();
                final int numberOfParams;
                numberOfParams = method.getParameterTypes().length;
                for (int i = 0; i < numberOfParams; i++ ) {
                    stringBuilder.append(paramValue + ", ");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 2));
                }
                return stringBuilder.toString();
            }
        }

    /*

#### ##    ## ######## ########  ######   ########     ###    ######## ####  #######  ##    ##       ######## ########  ######  ########
 ##  ###   ##    ##    ##       ##    ##  ##     ##   ## ##      ##     ##  ##     ## ###   ##          ##    ##       ##    ##    ##
 ##  ####  ##    ##    ##       ##        ##     ##  ##   ##     ##     ##  ##     ## ####  ##          ##    ##       ##          ##
 ##  ## ## ##    ##    ######   ##   #### ########  ##     ##    ##     ##  ##     ## ## ## ##          ##    ######    ######     ##
 ##  ##  ####    ##    ##       ##    ##  ##   ##   #########    ##     ##  ##     ## ##  ####          ##    ##             ##    ##
 ##  ##   ###    ##    ##       ##    ##  ##    ##  ##     ##    ##     ##  ##     ## ##   ###          ##    ##       ##    ##    ##
#### ##    ##    ##    ########  ######   ##     ## ##     ##    ##    ####  #######  ##    ##          ##    ########  ######     ##

     */
    public static class IntegrationTestGenerator {
        private final String className;
        private final String dtoClassName;
        private final String boClassName;
        private final String shortBoClassName;
        private final JCodeModel codeModel;
        private final boolean hasAttributes;
        private final List<FieldModel> fields;
        private final String shortClassNameUnderTest;
        private final Class classUnderTest;
        private final Map<String, String> foreignKeyTypes = new HashMap<String, String>();

        public IntegrationTestGenerator(String className, String boClassName, String dtoClassName, List<FieldModel> fields, Map<String, String> foreignKeyTypes, boolean hasAttributes) throws Exception {
            this.className = className;
            this.dtoClassName = dtoClassName;
            this.boClassName = boClassName;
            this.shortBoClassName = Util.determineShortName(boClassName);
            this.codeModel = new JCodeModel();
            this.hasAttributes = hasAttributes;
            this.fields = new LinkedList<FieldModel>(fields);
            if (boClassName.endsWith("KrmsTypeBo")) {
                this.classUnderTest = Class.forName((boClassName.substring(0, boClassName.indexOf("Bo")) + "BoServiceImpl").replace(".type.", "."));
            } else if (boClassName.endsWith("TypeTypeRelationBo")) {
                this.classUnderTest = Class.forName((boClassName.substring(0, boClassName.indexOf("Bo")) + "BoServiceImpl").replace(".typerelation.", "."));
            } else {
                this.classUnderTest = Class.forName((boClassName.substring(0, boClassName.indexOf("Bo"))
                        + "BoServiceImpl").replace(".reference.", "."));
            }
            this.shortClassNameUnderTest = classUnderTest.getSimpleName();
            this.foreignKeyTypes.putAll(foreignKeyTypes);
        }

        public String generate() throws Exception {
            byte[] javaCode = generateJava();
            System.out.println(new String(javaCode));
            return new String(javaCode);
        }

        private byte[] generateJava() throws Exception {
            JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
            //            classModel._implements(KrmsAttributeDefinitionService.class);
            Class<?> serviceInterface = null;
            if (boClassName.endsWith("KrmsTypeBo")) {
                serviceInterface = Class.forName(boClassName.replace("KrmsTypeBo", "KrmsTypeRepositoryService").replace(".impl.",".api."));
            } else if (boClassName.endsWith("TypeTypeRelationBo")) {
                serviceInterface = Class.forName((boClassName.substring(0, boClassName.indexOf("Bo")) + "BoService").replace(".typerelation.", "."));
            } else {
                serviceInterface = Class.forName((boClassName.substring(0, boClassName.lastIndexOf("Bo")) + "BoService").replace(".reference.", "."));
            }
//            Class<?> abstractBoTest = Class.forName("org.kuali.rice.krms.test.AbstractBoTest");
//            classModel._extends(abstractBoTest);
            classModel.javadoc().append("TODO extends org.kuali.rice.krms.test.AbstractBoTest\n");

            List<Method> byMethods = new LinkedList<Method>();
            Method[] serviceMethods = serviceInterface.getMethods();
            for (Method method : serviceMethods) {
                if (method.getName().indexOf("By") > -1) {
                    byMethods.add(method);
                }
            }

            renderClassAuthorJavadoc(classModel);
            renderCutField(classModel, classUnderTest, Class.forName(dtoClassName));
            renderGetter(classModel, Class.forName(dtoClassName));
            renderFields(classModel);
            renderBefore(classModel, classUnderTest);
            renderReuseByFailureTests(classModel, codeModel, byMethods, classUnderTest);
            renderTests(classModel, codeModel, Arrays.asList(serviceMethods), fields, classUnderTest, shortBoClassName);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            codeModel.build(new SingleStreamCodeWriter(outputStream));
            return outputStream.toByteArray();

        }

        private void renderFields(JDefinedClass classModel) throws Exception {
            if (hasAttributes) {
                JFieldVar ads = classModel.field(JMod.NONE, KrmsAttributeDefinitionService.class, "krmsAttributeDefinitionService");                
            }
            if (!foreignKeyTypes.isEmpty()) {
                for (String key :foreignKeyTypes.keySet()) {
                    String serviceImpl = null;
                    if (foreignKeyTypes.get(key).indexOf("Definition") > -1) {
                        serviceImpl = foreignKeyTypes.get(key).replace("Definition", "").replace(".type.", ".").replace(".api.", ".impl.") + "BoServiceImpl";
                    } else {
                        serviceImpl = foreignKeyTypes.get(key) + "BoServiceImpl";
                    }
                    // use the key to match the FieldMethod to get the type for the BoServiceImpl
                    try {
                        JFieldVar var = classModel.field(JMod.NONE, Class.forName(serviceImpl), Util.toLowerCaseFirstLetter(Util.determineShortName(serviceImpl)));
                    } catch (Exception e) {
                        // same types can result in creating the method more than once.
                    }
                }
            }
        }

        void renderGetter(JDefinedClass classModel, Class definitionClass) {
            JMethod method = classModel.method(JMod.NONE, definitionClass, Util.generateGetterName(definitionClass.getSimpleName(), false));
            method.body().directStatement("return " + Util.toLowerCaseFirstLetter(Util.determineShortName(definitionClass.getSimpleName())) + ";");
        }

        void renderBefore(JDefinedClass classModel, Class cutClass) {
            JMethod before = classModel.method(JMod.PUBLIC, codeModel.VOID, "setup");
            before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + " = new " + cutClass.getSimpleName() + "();");
            if (hasAttributes) {
                before.body().directStatement("krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();");
                before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + ".setAttributeDefinitionService(krmsAttributeDefinitionService);");
            }
            before.body().directStatement(Util.toLowerCaseFirstLetter(cutClass.getSimpleName()) + ".setBusinessObjectService(getBoService());");
            if (!foreignKeyTypes.isEmpty()) {
                Set<String> types = new HashSet<String>(); // set so we don't duplicate building services for types
                for (String key :foreignKeyTypes.keySet()) {
                    types.add(foreignKeyTypes.get(key));                    
                }
                for (String type: types) {
                    String varName = Util.determineShortName(type);
                    before.body().directStatement(Util.toLowerCaseFirstLetter(varName) + "BoServiceImpl = new " + varName +"BoServiceImpl();");
                    before.body().directStatement(Util.toLowerCaseFirstLetter(varName) + "BoServiceImpl.setBusinessObjectService(getBoService());");
                }
            }
            before.annotate(Before.class);
            before.javadoc().append("Note lower case u, do not override superclasses setUp");
        }

        static void renderReuseByFailureTests(JDefinedClass classModel, JCodeModel codeModel, List<Method> byMethods, Class classUnderTest) {
            String reusedTestClass = classUnderTest.getSimpleName().substring(0, classUnderTest.getSimpleName().indexOf("Bo")) + "BoServiceImplGenTest";
            for (Method method: byMethods) {
                JMethod nullMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_null_fail", IllegalArgumentException.class);
                nullMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_" + method.getName() + "_null_fail();");

                if ("String".equals(method.getReturnType().getSimpleName())) {
                    JMethod emptyMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_empty_fail", IllegalArgumentException.class);
                    emptyMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_" + method.getName() + "_empty_fail();");

                    JMethod whitespaceMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_whitespace_fail", IllegalArgumentException.class);
                    whitespaceMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_" + method.getName() + "_whitespace_fail();");
                }
            }
        }

        private static String resuseServiceImplTest(String reusedTestClass) {
            return "(" + reusedTestClass + ".create(" + Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + "))";
        }

        void renderTests(JDefinedClass classModel, JCodeModel codeModel, List<Method> methods, List<FieldModel> fields, Class classUnderTest, String shortBoClassName) {
            String reusedTestClass = classUnderTest.getSimpleName().substring(0, classUnderTest.getSimpleName().indexOf("Bo")) + "BoServiceImplGenTest";
            String findMethod = "find" + Util.determineShortName(className.substring(0, className.indexOf("Integ"))) + "Ids";

            for (Method method: methods) {
                if (method.getName().startsWith("create")) {
                    renderSuccessfulCreateTest(classModel, codeModel, reusedTestClass, method);
                    renderCreateExistingTest(classModel, codeModel, method);
                } else if (method.getName().startsWith("delete")) {
                    renderDeleteTest(classModel, codeModel, reusedTestClass, method);
                } else if (method.getName().startsWith("update")) {
                    renderUpdateTest(classModel, codeModel, reusedTestClass, method, fields);
                } else if (findMethod.equals(method.getName())) {
                    renderFindTest(classModel, codeModel, classUnderTest, findMethod);
                } else if ("from".equals(method.getName())) {
                    JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_from_null_yields_null");
                    JAnnotationUse annotation = testMethod.annotate(Test.class);
                    testMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_from_null_yields_null();");
                } else if ("to".equals(method.getName())) {
                    JMethod testFromMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_to");
                    JAnnotationUse annotationFrom = testFromMethod.annotate(Test.class);
                    testFromMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_to();");
                } else if (("get" + Util.determineShortName(method.getReturnType().getSimpleName())).equals(method.getName())) { // get by id
                    renderGetTest(classModel, codeModel, reusedTestClass, method);
                } else if (("find" + Util.determineShortName(method.getReturnType().getSimpleName())).equals(method.getName())) {
                    renderFindTest(classModel, codeModel, reusedTestClass, method);
                }
                // Do the expected failures in addition to any of these already done above
                if (method.getName().startsWith("create") || method.getName().startsWith("delete") || method.getName().startsWith("update")) {
                    JMethod testNullMethod = createExpectedExceptionTest(classModel, codeModel, "test_" + method.getName() + "_null_fail", IllegalArgumentException.class);
                    testNullMethod.body().directStatement(resuseServiceImplTest(reusedTestClass) + ".test_" + method.getName() + "_null_fail();");
                }

            }
        }

        private void renderSuccessfulCreateTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass,
                Method method) {
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName());
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            List<String> impleNames = createDependentServiceImpls(testMethod);
            StringBuilder sb = new StringBuilder();
            for (String name: impleNames) {
                sb.append(name).append(", ");
            }
            String implLine = "";
            if (sb.length() > 2) {
                implLine = sb.substring(0, sb.length() - 2);
            }
            testMethod.body().directStatement(
                    reusedTestClass + " test = " + reusedTestClass + ".create(" + Util.toLowerCaseFirstLetter(
                            reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ");");
            testMethod.body().directStatement("test.test_"+ method.getName() + "(" + implLine + ");");
            testMethod.body().directStatement(Util.toLowerCaseFirstLetter(Util.determineShortName(method.getReturnType().getSimpleName())) + " = test.get"+ method.getReturnType().getSimpleName() + "();");
            testMethod.body().directStatement("assert(" + Util.toLowerCaseFirstLetter(Util.determineShortName(method.getReturnType().getSimpleName())) + " != null);");
            testMethod.body().directStatement("assert(" + Util.toLowerCaseFirstLetter(Util.determineShortName(method.getReturnType().getSimpleName())) + ".getId() != null);");
        }

        private void renderCreateExistingTest(JDefinedClass classModel, JCodeModel codeModel, Method method) {
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName() + "_fail_existing");
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            annotation.param("expected", IllegalStateException.class);
            testMethod.body().directStatement("test_"+ method.getName() + "();");
            testMethod.body().directStatement("test_"+ method.getName() + "();");
        }

        private void renderGetTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass,
                Method method) {
            renderLookupTest(classModel, codeModel, reusedTestClass, method, "get");
        }

        private void renderFindTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass,
                Method method) {
            renderLookupTest(classModel, codeModel, reusedTestClass, method, "find");
        }

        private void renderLookupTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass, Method method, String verb) {
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName());
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            testMethod.body().directStatement("test_create" + reusedTestClass.substring(0, reusedTestClass.indexOf(
                    "Bo")) + "();");
            testMethod.body().directStatement(method.getReturnType().getSimpleName() + " def = "+ verb + method.getReturnType().getSimpleName() + "();");
            testMethod.body().directStatement(method.getReturnType().getSimpleName() + " def2 = " + Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + "." + verb + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "(def.getId());");
            testMethod.body().directStatement("assert(def2 != null);");
            testMethod.body().directStatement("assert(def2.equals(def));");
        }

        private void renderDeleteTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass,
                Method method) {
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName());
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            testMethod.body().directStatement("test_create" + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "();");
            String defClassName = method.getName().substring(6, method.getName().length());
            if (Util.IS_KRMS) {
                defClassName = defClassName + "Definition";
            }
            testMethod.body().directStatement(defClassName + " def = get"+ defClassName + "();");
            testMethod.body().directStatement("String id = def.getId();");
            testMethod.body().directStatement(Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ".delete" + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "(id);");
            testMethod.body().directStatement(defClassName + " def2 = " + Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ".get" + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "(id);");
            testMethod.body().directStatement("assert(def2 == null);");
        }

        private void renderUpdateTest(JDefinedClass classModel, JCodeModel codeModel, String reusedTestClass,
                Method method, List<FieldModel> fields) {
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_" + method.getName());
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            testMethod.body().directStatement("test_create" + reusedTestClass.substring(0, reusedTestClass.indexOf(
                    "Bo")) + "();");
            String defClassName = method.getName().substring(6, method.getName().length());
            if (Util.IS_KRMS) {
                defClassName = defClassName + "Definition";
            }
            testMethod.body().directStatement(defClassName + " def = get"+ defClassName + "();");
            testMethod.body().directStatement("String id = def.getId();");

            // fields ending in name generally do not have foreign key constraints
            FieldModel nameField = null;
            for (FieldModel field: fields) {
                if (field.fieldName.endsWith("ame")) {
                    nameField = field;
                    break;
                }
            }
            String getterName = null;
            String setterName = null;
            if (nameField == null) {
                testMethod.body().directStatement("assert(false); // remove once TODO below is done");
                testMethod.body().directStatement("/* TODO change getterMethod and setterMethod to an appropriate field for the update test");
                getterName = "getterMethod";
                setterName = "setterMethod.";
            } else {
                getterName = Util.generateGetterName(nameField.fieldName, nameField.fieldType == Boolean.class);
                setterName = Util.generateSetterName(nameField.fieldName);
            }
            testMethod.body().directStatement("assert(!\"UpdateTest\".equals(def." + getterName + "()));");
            testMethod.body().directStatement(reusedTestClass.substring(0, reusedTestClass.indexOf("Service")) + " bo = " + Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ".from(def);");
            testMethod.body().directStatement("bo." + setterName + "(\"UpdateTest\");");
            testMethod.body().directStatement(Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ".update" + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "(" + defClassName + ".Builder.create(bo).build());");
            testMethod.body().directStatement(defClassName + " def2 = " + Util.toLowerCaseFirstLetter(reusedTestClass.substring(0, reusedTestClass.indexOf("GenTest"))) + ".get" + reusedTestClass.substring(0, reusedTestClass.indexOf("Bo")) + "(id);");
            testMethod.body().directStatement("assert(\"UpdateTest\".equals(def2." + getterName + "()));");
            if (nameField == null) {
                testMethod.body().directStatement("*/");
            }
        }

        private void renderFindTest(JDefinedClass classModel, JCodeModel codeModel, Class classUnderTest, String methodName) {
            String implInstance = Util.toLowerCaseFirstLetter(classUnderTest.getSimpleName());
            JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, "test_Find");
            JAnnotationUse annotation = testMethod.annotate(Test.class);
            testMethod.body().directStatement("QueryByCriteria.Builder query = QueryByCriteria.Builder.create();");
            testMethod.body().directStatement("query.setPredicates(PredicateFactory.equal(\"id\", \"ID\"));");
            testMethod.body().directStatement("CriteriaLookupServiceImpl criteria = new CriteriaLookupServiceImpl();");
            testMethod.body().directStatement("criteria.setCriteriaLookupDao(new CriteriaLookupDaoProxy());");
            testMethod.body().directStatement(implInstance + ".setCriteriaLookupService(criteria);");
            testMethod.body().directStatement("assert("+ implInstance + "." + methodName + "(query.build()).isEmpty());");
            testMethod.body().directStatement("test_createReferenceObjectBinding();");
            testMethod.body().directStatement("assert(!" + implInstance + "." + methodName + "(query.build()).isEmpty());");
            testMethod.body().directStatement("assert(\"ID\".equals(" + implInstance + "." + methodName + "(query.build()).get(0)));");
        }

        private List<String> createDependentServiceImpls(JMethod testMethod) {
            if (!foreignKeyTypes.isEmpty()) {
                List<String> implNames = new LinkedList<String>();
                Set<String> types = new HashSet<String>(); // set so we don't duplicate building services for types
                for (String key : foreignKeyTypes.keySet()) {
                    types.add(foreignKeyTypes.get(key));
                }
                for (String type: types) {
                    String varName = Util.determineShortName(type);
                    implNames.add(Util.toLowerCaseFirstLetter(varName));
                    testMethod.body().directStatement(varName + "IntegrationGenTest " + Util.toLowerCaseFirstLetter(
                            varName) + "Test = new " + varName + "IntegrationGenTest();");
                    testMethod.body().directStatement(Util.toLowerCaseFirstLetter(varName) + "Test.setup(); // Note lowercase u");
                    testMethod.body().directStatement(Util.toLowerCaseFirstLetter(varName) + "Test.test_create" + varName + "();");
                    testMethod.body().directStatement(Util.determineShortClassName(type) + " " + Util.toLowerCaseFirstLetter(varName) + " = " + Util.toLowerCaseFirstLetter(varName) + "Test.get" + varName + "();");

                }
                return implNames;
            }
            return Collections.emptyList();
        }

    }

    /**
     #######                           #     #
     #       # ###### #      #####     ##   ##  ####  #####  ###### #
     #       # #      #      #    #    # # # # #    # #    # #      #
     #####   # #####  #      #    #    #  #  # #    # #    # #####  #
     #       # #      #      #    #    #     # #    # #    # #      #
     #       # #      #      #    #    #     # #    # #    # #      #
     #       # ###### ###### #####     #     #  ####  #####  ###### ######
     */
    public static class FieldModel {

        final String fieldName;
        final Class<?> fieldType;

        FieldModel(String fieldName, Class<?> fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FieldModel)) {
                return false;
            }

            final FieldModel that = (FieldModel) o;

            if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
                return false;
            }
            if (fieldType != null ? !fieldType.equals(that.fieldType) : that.fieldType != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = fieldName != null ? fieldName.hashCode() : 0;
            result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
            return result;
        }
    }


    /*
    #     #
    #     # ##### # #
    #     #   #   # #
    #     #   #   # #
    #     #   #   # #
    #     #   #   # #
     #####    #   # ######
     */
    // Utility methods that use sun's codeModel classes or the private class FieldModel
    static List<FieldModel> determineFields(Class<?> contractInterface) throws Exception {
        List<FieldModel> fieldModels = new ArrayList<FieldModel>();

        Method[] methods = contractInterface.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            String fieldName = null;
            if (method.getReturnType() != Void.class && method.getParameterTypes().length == 0) {
                if (methodName.startsWith("get")) {
                    fieldName = Util.toLowerCaseFirstLetter(methodName.substring(3));
                } else if (methodName.startsWith("is")) {
                    fieldName = Util.toLowerCaseFirstLetter(methodName.substring(2));
                } else {
                    continue;
                }
                fieldModels.add(new FieldModel(fieldName, method.getReturnType()));
            }
        }

        return fieldModels;
    }

    static Set<FieldModel> determineFieldsSet(Class<?> contractInterface) throws Exception {
        Set<FieldModel> fieldModels = new HashSet<FieldModel>();

        Method[] methods = contractInterface.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            String fieldName = null;
            if (method.getReturnType() != Void.class && method.getParameterTypes().length == 0) {
                if (methodName.startsWith("get")) {
                    fieldName = Util.toLowerCaseFirstLetter(methodName.substring(3));
                } else if (methodName.startsWith("is")) {
                    fieldName = Util.toLowerCaseFirstLetter(methodName.substring(2));
                } else {
                    continue;
                }
                fieldModels.add(new FieldModel(fieldName, method.getReturnType()));
            }
        }

        return fieldModels;
    }

    static boolean contains(List<FieldModel> fields, String methodName) {
        for (FieldModel field: fields) {
            if (field.fieldName.equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    static JMethod createMethodWithParam(JDefinedClass classModel, Object returnClass, Class paramClass, String paramName, String methodName) {
        JMethod method = null;
        if (returnClass instanceof JPrimitiveType) {
            method = classModel.method(JMod.PUBLIC, (JPrimitiveType)returnClass, methodName);
        } else if (returnClass instanceof Class) {
            method = classModel.method(JMod.PUBLIC, (Class)returnClass, methodName);
        } else if (returnClass instanceof JClass) {
            method = classModel.method(JMod.PUBLIC, (JClass)returnClass, methodName);
        } else {
            throw new RuntimeException("createMethodWithParam returnClass must be of type Class, JPrimitiveType, or JClass was class: " + returnClass.getClass() );
        }
        method.param(paramClass, Util.toLowerCaseFirstLetter(paramName));
        return method;
    }

    static JMethod createMethodWithParam(JDefinedClass classModel, Object returnClass, JClass paramClass, String paramName, String methodName) {
        JMethod method = null;
        if (returnClass instanceof JPrimitiveType) {
            method = classModel.method(JMod.PUBLIC, (JPrimitiveType)returnClass, methodName);
        } else if (returnClass instanceof Class) {
            method = classModel.method(JMod.PUBLIC, (Class)returnClass, methodName);
        } else if (returnClass instanceof JClass) {
            method = classModel.method(JMod.PUBLIC, (JClass)returnClass, methodName);
        } else {
            throw new RuntimeException("createMethodWithParam returnClass must be of type Class, JPrimitiveType, or JClass was class: " + returnClass.getClass() );
        }
        method.param(paramClass, Util.toLowerCaseFirstLetter(paramName));
        return method;
    }

    static JMethod createMethodWithParam(JDefinedClass classModel, Object returnClass, Class paramClass, String methodName) {
        return createMethodWithParam(classModel, returnClass, paramClass, Util.determineShortName(paramClass.getSimpleName()), methodName);
    }

    /**
     * Assuming something is a String can get you in trouble... this method might be more trouble than it is worth, should maybe just use
     * one of the other createMethodWithParam methods.
     * @param classModel
     * @param returnClass
     * @param paramName
     * @param methodName
     * @return
     */
    static JMethod createMethodWithStringParam(JDefinedClass classModel, Object returnClass, String paramName, String methodName) {
        JMethod method = null;
        if (returnClass instanceof JPrimitiveType) {
            method = classModel.method(JMod.PUBLIC, (JPrimitiveType)returnClass, methodName);
        } else if (returnClass instanceof Class) {
            method = classModel.method(JMod.PUBLIC, (Class)returnClass, methodName);
        } else if (returnClass instanceof JClass){
            method = classModel.method(JMod.PUBLIC, (JClass)returnClass, methodName);
        } else {
            throw new RuntimeException("createMethodWithStringParam returnClass must be of type Class, JPrimitiveType, or JClass was class: " + returnClass.getClass());
        }
        method.param(String.class, Util.toLowerCaseFirstLetter(paramName));
        return method;
    }

    static JClass newMapStringStringClass(JCodeModel codeModel) {
        List<JClass> narrows = new LinkedList<JClass>();
        narrows.add(codeModel.ref(String.class));
        narrows.add(codeModel.ref(String.class));
        JClass rawClass = codeModel.ref(Map.class);
        return rawClass.narrow(narrows);
    }

    /**
     * Generate an Object's set ID guard.  These ids can be null, but not blank.
     * @param fieldName
     * @param method
     */
    static void renderIsNotNullAndBlankIllegalArgumentExceptionGuard(String fieldName, JMethod method) {
        method.body().directStatement("if (" + fieldName + " != null && org.apache.commons.lang.StringUtils.isBlank(" + fieldName + ")) {");
        method.body().directStatement("    throw new IllegalArgumentException(\"" + fieldName + " is blank\");");
        method.body().directStatement("}");
    }

    static void renderIsNullIllegalArgumentExceptionGuard(String fieldName, JMethod method) {
        method.body().directStatement("if (" + fieldName + " == null) {");
        method.body().directStatement("    throw new IllegalArgumentException(\"" + fieldName + " is null\");");
        method.body().directStatement("}");
    }

    static void renderStringUtilsIsBlankIllegalArgumentExceptionGuard(String fieldName, JMethod method) {
        method.body().directStatement("if (org.apache.commons.lang.StringUtils.isBlank(" + fieldName + ")) {");
        method.body().directStatement("    throw new IllegalArgumentException(\"" + fieldName + " is null or blank\");");
        method.body().directStatement("}");
    }

    static void renderBuildAttributeCollection(JDefinedClass classModel, String baseClassName, Class attrBoClass, Class dtoClass, String className, JCodeModel codeModel, int jmod) throws Exception {
        JClass returnClass = narrow(attrBoClass, Collection.class, codeModel);
//        JClass returnClass = narrow(Class.forName(className.substring(0, className.indexOf("Bo"))
//                + "AttributeBo"), Collection.class, codeModel);

        JMethod method = classModel.method(jmod, returnClass, "buildAttributes");
        method.param(dtoClass, "im");
        JClass paramClass = narrow(attrBoClass, Collection.class, codeModel);
//        JClass paramClass = narrow(Class.forName(className.substring(0, className.indexOf("Bo"))
//                + "AttributeBo"), Collection.class, codeModel);
        method.param(paramClass, "attributes");
        JBlock methodBody = method.body();
        methodBody.directStatement("// build a map from attribute name to definition");
        methodBody.directStatement("Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();\n");

        methodBody.directStatement("List<KrmsAttributeDefinition> attributeDefinitions = getAttributeDefinitionService().findAttributeDefinitionsByType(im.getTypeId());\n");
        methodBody.directStatement("for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {");
        methodBody.directStatement("    attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);");
        methodBody.directStatement("}\n");
        methodBody.directStatement("// for each entry, build a " + baseClassName + "AttributeBo and add it");
        methodBody.directStatement("if (im.getAttributes() != null) {");
        methodBody.directStatement("    for (Map.Entry<String,String> entry  : im.getAttributes().entrySet()) {");
        methodBody.directStatement("        KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey());\n");
        methodBody.directStatement("        if (attrDef != null) {");
        methodBody.directStatement("            " + baseClassName + "AttributeBo attributeBo = new " + baseClassName + "AttributeBo();");
        methodBody.directStatement("            attributeBo.set" + baseClassName + "Id( im.getId() );");
        methodBody.directStatement("            attributeBo.setAttributeDefinitionId(attrDef.getId());");
        methodBody.directStatement("            attributeBo.setValue(entry.getValue());");
        methodBody.directStatement("            attributeBo.setAttributeDefinition(KrmsAttributeDefinitionBo.from(attrDef));");
        methodBody.directStatement("            attributes.add(attributeBo);");
        methodBody.directStatement("        } else {");
        methodBody.directStatement("            throw new RiceIllegalStateException(\"there is no attribute definition with the name '\" +\n"
                + "                                 entry.getKey() + \"' that is valid for the " + Util.toLowerCaseFirstLetter(baseClassName) + " type with id = '\" + im.getTypeId() +\"'\");");
        methodBody.directStatement("        }");
        methodBody.directStatement("    }");
        methodBody.directStatement("}");
        methodBody.directStatement("return attributes;");
    }

    static void renderBuildAttributeBoSet(JDefinedClass classModel, Class attrBoClass, Class dtoClass, JCodeModel codeModel, int jmod) throws Exception{
        JClass returnClass = narrowedSet(attrBoClass, codeModel);

        JMethod method = classModel.method(jmod, returnClass, "buildAttributeBoSet");
        method.param(dtoClass, "im");
        JBlock methodBody = method.body();
        methodBody.directStatement("Set<" + attrBoClass.getSimpleName() + "> attributes = new HashSet<" + attrBoClass.getSimpleName() + ">();");
        methodBody.directStatement("return (Set)buildAttributes(im, attributes);");
    }

    static void renderBuildAttributeBoList(JDefinedClass classModel, Class attrBoClass, Class dtoClass, JCodeModel codeModel, int jmod) throws Exception{
        JClass returnClass = narrowedList(attrBoClass, codeModel);

        JMethod method = classModel.method(jmod, returnClass, "buildAttributeBoList");
        method.param(dtoClass, "im");
        JBlock methodBody = method.body();
        methodBody.directStatement("List<" + attrBoClass.getSimpleName() + "> attributes = new LinkedList<" + attrBoClass.getSimpleName() + ">();");
        methodBody.directStatement("return (List)buildAttributes(im, attributes);");
    }


    static JClass narrowedSet(Class classToNarrowTo, JCodeModel codeModel) {
        return narrow(classToNarrowTo, Set.class, codeModel);
    }

    static JClass narrowedList(Class classToNarrowTo, JCodeModel codeModel) {
        return narrow(classToNarrowTo, List.class, codeModel);
    }

    static JClass narrow(Class classToNarrowTo, Class classToNarrow, JCodeModel codeModel){
        JClass narrowedClass = codeModel.ref(classToNarrowTo);
        JClass rawClass = codeModel.ref(classToNarrow);
        return rawClass.narrow(narrowedClass);
    }

    static void renderCutField(JDefinedClass classModel, Class cutClass, Class cutDtoClass) throws Exception{
        JFieldVar typeNameField = classModel.field(JMod.NONE, cutClass, Util.toLowerCaseFirstLetter(cutClass.getSimpleName()));
//        JFieldVar typeNameFieldDto = classModel.field(JMod.NONE, cutDtoClass, "def");
        JFieldVar typeNameFieldDto = classModel.field(JMod.NONE, cutDtoClass, Util.toLowerCaseFirstLetter(cutDtoClass.getSimpleName()));
    }

    static JMethod createExpectedExceptionTest(JDefinedClass classModel, JCodeModel codeModel, String testName,
            Class expectedExceptionClass) {
        JMethod testMethod = classModel.method(JMod.PUBLIC, codeModel.VOID, testName);
        JAnnotationUse annotation = testMethod.annotate(Test.class);
        annotation.param("expected", expectedExceptionClass);
        return testMethod;
    }

    static void renderClassAuthorJavadoc(JDefinedClass classModel) {
        JDocComment javadoc = classModel.javadoc();
        javadoc.append(Util.CLASS_AUTHOR_JAVADOC);
    }

    private static List<FieldModel> sort(List<FieldModel> fields) {
        List<FieldModel> sortedFields = new LinkedList<FieldModel>();
        sortedFields.addAll(fields);
        Collections.sort(sortedFields, new Comparator<FieldModel>() {
            @Override
            public int compare(FieldModel fieldModel, FieldModel fieldModel1) {
                return fieldModel.fieldName.compareTo(fieldModel1.fieldName);
            }
        });
        return sortedFields;
    }


    /**
     *
     * @param fields list of FieldModels
     * @param createMethod the method to add params to.  If null, only the params string will still be generated
     * @return String representation of the create parameters
     */
    static String builderCreate(List<FieldModel> fields, JMethod createMethod) {
        StringBuilder paramsBuilder = new StringBuilder();
        for (FieldModel fieldModel : fields) {
            if (!"id".equals(fieldModel.fieldName)) {
                if (Util.VALIDATED_FIELDS_NOT_BLANK.contains(fieldModel.fieldName)) {
                    if (createMethod != null) {
                        createMethod.param(fieldModel.fieldType, fieldModel.fieldName);
                    }
                    paramsBuilder.append(fieldModel.fieldName).append(", ");
                }
            }
        }
        if (paramsBuilder.toString().endsWith(", ")) {
            paramsBuilder = new StringBuilder(paramsBuilder.toString().substring(0, paramsBuilder.toString().length() - 2));
        }
        return paramsBuilder.toString();
    }

}

