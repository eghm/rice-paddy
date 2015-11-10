Kuali Rice 2.1 JPA code generator ImmutableJaxbGenerator.

To use, these files need to be in the developer-tools and dependencies to kuali modules created.

Instructions for use are in the ImmutableJaxbGenerator class javadoc.  As com.sun.codelmodel is used newly generated classes that depend on each other will not be found.  They need to be created (as empty classes is fine) and the generator rerun on them.

The whole working project can be found at https://svn.kuali.org/repos/rice/sandbox/rice-2.1-ks-krms/ as my updates to ImmutableJaxbGenerator created new dependencies of rice modules on developer-tools it wasn't merged back to trunk.