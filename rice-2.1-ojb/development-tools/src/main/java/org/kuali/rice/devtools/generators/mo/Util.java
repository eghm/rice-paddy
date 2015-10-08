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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Util class for Code Generation.  References no classes outside java.util or java.lang.reflect for ease of reuse.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
final class Util {

    public static final Boolean IS_KRMS = false; // KRMS used a different naming convention see KULRICE-7373
    public static final Boolean USE_SET = false; // Set's have some problems someplace I hear.
    public static final String VERSION_NUMBER_FIELD = "versionNumber";
    public static final String OBJECT_ID_FIELD = "objectId";

    public static final String PRIVATE_JAXB_CONSTRUCTOR_JAVADOC = "Private constructor used only by JAXB. This " +
            "constructor should never be called.\nIt is only present for use during JAXB unmarshalling.";
    public static final String BUILDER_CONSTRUCTOR_JAVADOC = "Constructs an object from the given builder.  This " +
            "constructor is private and should only ever be invoked from the builder.\n\n@param builder the Builder " +
            "from which to construct the object.";
    public static final String CLASS_AUTHOR_JAVADOC = "@author Kuali Rice Team (rice.collab@kuali.org)";
    public static final String CONSTANTS_CLASS_NAME = "Constants";
    public static final String ROOT_ELEMENT_NAME_FIELD = "ROOT_ELEMENT_NAME";
    public static final String TYPE_NAME_FIELD = "TYPE_NAME";
    public static final String TYPE_NAME_SUFFIX = "Type";
    public static final String HASH_CODE_EQUALS_EXCLUDE_FIELD = "HASH_CODE_EQUALS_EXCLUDE";
    public static final String COMMON_ELEMENTS_CLASS = "CommonElements";
    public static final String FUTURE_ELEMENTS_FIELD = "FUTURE_ELEMENTS";
    public static final String CONSTANTS_CLASS_JAVADOC = "Defines some internal constants used on this class.";

    public static final String ELEMENTS_CLASS_NAME = "Elements";
    public static final String ELEMENTS_CLASS_JAVADOC = "A private class which exposes constants which define the XML element " +
            "names to use when this object is marshalled to XML.";
    public static final List<String> COMMON_ELEMENTS = Arrays.asList(VERSION_NUMBER_FIELD, OBJECT_ID_FIELD);

    public static final String BUILDER_CLASS_NAME = "Builder";
    public static final List<String> UNVALIDATED_FIELD_SETS = Arrays.asList("active", VERSION_NUMBER_FIELD);
    /** sequenceNumber is not always NOT NULL (see KRMS Action) use -DNOT_BLANK=sequenceNumber if your sequenceNumber is NOT NULL */
    public static List<String> VALIDATED_FIELDS_NOT_BLANK = new LinkedList<String>(Arrays.asList("id", "namespace")); // OBJECT_ID_FIELD?
    public static final List<String> COLLECTION_CLASSES = Arrays.asList(Map.class.getSimpleName(), List.class.getSimpleName(),
            Set.class.getSimpleName());

    static Long currentLong = 0L;
    public static Long nextLong() {
        return currentLong++;
    }

    /**
     * set -DENUM to comma delimited list of name:enum1:enum2 (i.e. -DENUM=RelationshipType:UNKNOWN:USAGE_ALLOWED,Another:VALUE1:VALUE2 )
     * @return empty is none
     */
    public static Map<String, List<String>> getEnumSystemProperties() {
        Map<String, List<String>> enumsValues = new HashMap<String, List<String>>();
        if (System.getProperty("ENUM") != null) {
            StringTokenizer tokenizer = new StringTokenizer(System.getProperty("ENUM"), ",");
            while (tokenizer.hasMoreTokens()) {
                List<String> enumValues = new LinkedList<String>();
                String nameValues = tokenizer.nextToken();
                if (nameValues.indexOf(":") == -1) {
                    throw new IllegalArgumentException("ENUM error no colon");
                } else {
                    String name = nameValues.substring(0, nameValues.indexOf(":"));
                    String values = nameValues.substring(name.length() + 1, nameValues.length());
                    StringTokenizer colenizer = new StringTokenizer(values, ":");
                    while (colenizer.hasMoreTokens()) {
                        enumValues.add(colenizer.nextToken());
                    }
                    enumsValues.put(name, enumValues);
                }
            }
        }
        return enumsValues;
    }

    /**
     * set -DFOREIGN_KEY to comma delimited list of name:type (i.e. -DFOREIGN_KEY=toTypeId:KrmsType,fromTypeId:KrmsType
     * @return empty is none
     */
    public static Map<String, String> getForeignKeySystemProperty() {
        Map<String, String> foreignKeyTypes = new HashMap<String, String>();
        if (System.getProperty("FOREIGN_KEY") != null) {
            StringTokenizer tokenizer = new StringTokenizer(System.getProperty("FOREIGN_KEY"), ",");
            while (tokenizer.hasMoreTokens()) {
                String keyType = tokenizer.nextToken();
                if (keyType.indexOf(":") == -1) {
                    throw new IllegalArgumentException("FOREIGN_KEY error no colon");
                }
                foreignKeyTypes.put(keyType.substring(0, keyType.indexOf(":")), keyType.substring(keyType.indexOf(":")
                        + 1, keyType.length()));
            }
        }
        return foreignKeyTypes;
    }

    /**
     * set -DNOT_BLANK to comma delimited list. (i.e. -DNOT_BLANK=toType,fromType ) They get added to Util.VALIDATED_FIELDS_NOT_BLANK
     */
    public static void getNotBlankSystemProperty() {
        if (System.getProperty("NOT_BLANK") != null) {
            StringTokenizer tokenizer = new StringTokenizer(System.getProperty("NOT_BLANK"), ",");
            while (tokenizer.hasMoreTokens()) {
                Util.VALIDATED_FIELDS_NOT_BLANK.add(tokenizer.nextToken());
            }
        }
    }

    public static String getGenerationProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("ENUM") != null ? "-DENUM=" + System.getProperty("ENUM") + " ":"");
        sb.append(System.getProperty("NOT_BLANK") != null ? "-DNOT_BLANK=" + System.getProperty("NOT_BLANK") + " ":"");
        sb.append(System.getProperty("FOREIGN_KEY") != null ? "-DFOREIGN_KEY=" + System.getProperty("FOREIGN_KEY") + " ":"");
        return sb.toString();
    }


    public static String toLowerCaseFirstLetter(String value) {
		return value.substring(0, 1).toLowerCase() + value.substring(1);
	}

    public static String toUpperCaseFirstLetter(String value) {
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

    public static String toConstantsVariable(String fieldName) {
		StringBuilder constantVariable = new StringBuilder();
		// just to be safe
		fieldName = toLowerCaseFirstLetter(fieldName);
		StringBuilder segAccum = new StringBuilder();
		for (char character : fieldName.toCharArray()) {
			if (Character.isUpperCase(character)) {
				constantVariable.append(segAccum.toString().toUpperCase());
				constantVariable.append("_");
				segAccum = new StringBuilder();
			}
			segAccum.append(character);
		}
		// do the last bit
		constantVariable.append(segAccum.toString().toUpperCase());
		return constantVariable.toString();
	}

    public static boolean isBoolean(Class<?> clazz) {
        return clazz == Boolean.TYPE || clazz == Boolean.class;
    }

    public static String generateGetterName(String fieldName, boolean is) {
		return (is ? "is" : "get") + Util.toUpperCaseFirstLetter(fieldName);
	}

    public static String generateGetter(String fieldName, boolean is) {
		return generateGetterName(fieldName, is) + "()";
	}

    public static String generateSetterName(String fieldName) {
		return "set" + Util.toUpperCaseFirstLetter(fieldName);
	}

    public static String generateGetterJavadoc(String fieldName, String className) {
        return "This is the " + Util.toUpperCaseFirstLetter(fieldName) + " of the " + className + "\n"
                + "<p>\n"
                + "The " + Util.toUpperCaseFirstLetter(fieldName) + " of the " + className + "\n"
                + "</p>\n"
                + "@return the " + Util.toUpperCaseFirstLetter(fieldName) + " of the " + className;
    }

    public static String generateSetter(String fieldName, String valueToSet) {
		return generateSetterName(fieldName) + "(" + valueToSet + ")";
	}

    public static String generateSetterJavadoc(String fieldName) {
        return "Sets the value of " + fieldName + " to the given value.\n\n@param " +
                Util.toLowerCaseFirstLetter(fieldName) + " the " + fieldName + " value to set.";
    }

    public static String generateBuilderSetterJavadoc(String fieldName) {
        return "Sets the value of " + fieldName + " on this builder to the given value.\n\n@param " +
                Util.toLowerCaseFirstLetter(fieldName) + " the " + fieldName + " value to set.";
    }

    public static boolean isCommonElement(String fieldName) {
		return COMMON_ELEMENTS.contains(fieldName);
	}

    public static String generateBuilderJavadoc(String simpleClassName, String simpleContractClassName) {
		return "A builder which can be used to construct {@link " + simpleClassName + "} instances.  " +
			"Enforces the constraints of the {@link " + simpleContractClassName + "}.";
	}

    public static String generateBoClassJavadoc(String className) {
        String interfaceName, immutableName = null;
        if (IS_KRMS) {
            interfaceName = className.substring(0, className.lastIndexOf("Bo")) + "DefinitionContract";
            immutableName = className.substring(0, className.lastIndexOf("Bo")) + "Definition";
        } else {
            interfaceName = className.substring(0, className.lastIndexOf("Bo")) + "Contract";
            immutableName = className.substring(0, className.lastIndexOf("Bo"));
        }

        // @see service if not an AttributeBo
        String serviceName = className + "Service";
        String see = "@see " + serviceName + "\n\n";
        if (className.endsWith("AttributeBo")) {
            see = "";
        }

        return "The mutable implementation of the @{link " + interfaceName + "} interface, the counterpart to the immutable implementation {@link "
                + immutableName + "}.\n"
                + Util.CLASS_AUTHOR_JAVADOC;
    }

    public static String generateServiceJavadoc(String serviceName) {
        return "This is the interface for accessing repository {@link "
                + serviceName.substring(0, serviceName.lastIndexOf("Service")) // Naming convention AgendaBoService, etc.
                + "} related business objects.\n\n" + CLASS_AUTHOR_JAVADOC;
    }

    public static String generateServiceImplJavadoc(String serviceImplName) {
        return "Implementation of the @{link " + serviceImplName.substring(0, serviceImplName.lastIndexOf("Impl"))
                + "} interface for accessing  {@link "
                +  serviceImplName.substring(0, serviceImplName.lastIndexOf("ServiceImpl")) // Naming convention AgendaBoServiceImpl, etc.
                + "} related business objects.\n\n" + CLASS_AUTHOR_JAVADOC;
    }

    public static String generateAttributeBoJavadoc(String shortBoClassName) {
        if (shortBoClassName.contains("Attribute")) {
            shortBoClassName = shortBoClassName.substring(0, shortBoClassName.indexOf("Attribute"));
        }
        return "This class represents a " + shortBoClassName + "Attribute business object.\n"
                + " " + shortBoClassName + "AttributeBos provide a way to attach custom data to a "
                + shortBoClassName + " based on the "
                + shortBoClassName + "'s type.\n\n"
                + Util.CLASS_AUTHOR_JAVADOC;
    }

    public static String determineShortName(String dtoClassName) {
        String shortClassName = dtoClassName;
        shortClassName = Util.shortClassName(shortClassName);
        if (shortClassName.endsWith("Definition")) { // KRMS only
            shortClassName = shortClassName.substring(0, shortClassName.lastIndexOf("Definition"));
        }
        return shortClassName;
    }

    public static String determineShortClassName(String dtoClassName) {
        String shortClassName = dtoClassName;
        shortClassName = Util.shortClassName(shortClassName);
        return shortClassName;
    }

    public static List<Method> determineMethods(Class<?> contractInterface) throws Exception {
        List<Method> methodList = new ArrayList<Method>();

        Method[] methods = contractInterface.getMethods();
        for (Method method : methods) {
            methodList.add(method);
        }

        return methodList;
    }


    public static String shortClassName(String shortClassName) {
        if (shortClassName.indexOf(".") > -1) {
            shortClassName = shortClassName.substring(shortClassName.lastIndexOf(".") + 1, shortClassName.length());
        }
        return shortClassName;
    }

    public static String getInitString(Class<?> clazz) {
        if (clazz == Boolean.TYPE) {
            return "false";
        } else if (clazz == Character.TYPE) {
            return "'\\\\u0000'";
        } else if (clazz == Long.TYPE) {
            return "0L";
        } else if (clazz == Float.TYPE) {
            return "0.0F";
        } else if (clazz == Double.TYPE) {
            return "0.0D";
        } else if (clazz == Byte.TYPE || clazz == Short.TYPE || clazz == Integer.TYPE) {
            return "0";
        } else {
            return "null";
        }
    }

    public static String generateCreateJavadoc(String dtoClassName, String shortClassName, String lowerCaseClassName) {
        return "This will create a {@link " + Util.shortClassName(dtoClassName) + "} exactly like the parameter passed in.\n\n"
                + "@param " + lowerCaseClassName + "  The " + Util.shortClassName(dtoClassName) + " to create.\n"
                + "@throws IllegalArgumentException if the " + shortClassName + " is null.\n"
                + "@throws IllegalStateException if the " + shortClassName + " already exists in the system.\n"
                + "@return a {@link " + Util.shortClassName(dtoClassName) + "} exactly like the parameter passed in.";
    }

    public static String generateReadJavadoc(String dtoClassName) {
        return "Retrieves a " + Util.shortClassName(dtoClassName) + " from the repository based on the given id.\n\n"
                + "@param " + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClassName)) + "Id to retrieve.\n"
                + "@return a {@link " + Util.shortClassName(dtoClassName) + "} identified by the given id.  \n"
                + "A null reference is returned if an invalid or non-existent id is supplied.";
    }

    public static String generateUpdateJavadoc(String dtoClassName, String dtoShortClassName,
            String dtoLowerCaseClassName) {
        return "This will update an existing {@link " + Util.shortClassName(dtoClassName) + "}.\n\n"
                + "@param " + dtoLowerCaseClassName + "  The " + dtoShortClassName + " to update.\n"
                + "@throws IllegalArgumentException if the " + dtoShortClassName + " is null.\n"
                + "@throws IllegalStateException if the " + dtoShortClassName + " does not exists in the system.";
    }

    public static String generateDeleteJavadoc(String dtoClassName, String dtoShortClassName) {
        return "Delete the {@link " + shortClassName(dtoClassName) + "} with the given id.\n\n"
                + "@param " + Util.toLowerCaseFirstLetter(dtoShortClassName) + "Id to delete.\n"
                + "@throws IllegalArgumentException if the " + dtoShortClassName + " is null.\n"
                + "@throws IllegalStateException if the " + dtoShortClassName + " does not exists in the system";
    }

    public static String generateToJavadoc(Class boClass, Class dtoClass) {
        return generateToJavadoc(boClass.getSimpleName(), dtoClass.getSimpleName());
    }

    public static String generateToJavadoc(String boClassName, String dtoClassName) {
        return "Converts a mutable {@link " + boClassName + "}"
                + " to its immutable counterpart, {@link " + dtoClassName + "}.\n"
                + "@param " + Util.toLowerCaseFirstLetter(boClassName) + " the mutable business object.\n"
                + "@return a {@link " + dtoClassName + "} the immutable object.";
    }

    public static String generateFromJavadoc(Class boClass, Class dtoClass) {
        return generateFromJavadoc(boClass.getSimpleName(), dtoClass.getSimpleName());
    }

    public static String generateFromJavadoc(String boClassName, String dtoClassName) {
        return "Converts a immutable {@link " + dtoClassName + "} to its mutable {@link " + boClassName + "} counterpart.\n"
                + "@param " + Util.toLowerCaseFirstLetter(Util.determineShortName(dtoClassName)) + " the immutable object.\n"
                + "@return a {@link " + boClassName + "} the mutable " + boClassName + ".";
    }
}
