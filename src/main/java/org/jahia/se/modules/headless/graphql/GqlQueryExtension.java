package org.jahia.se.modules.headless.graphql;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class GqlQueryExtension {

    @GraphQLField
    @GraphQLName("npm")
    @GraphQLDescription("Main access field to the DX GraphQL Form API")
    public static GqlNpmHelper getNpmHelper() {
        return new GqlNpmHelper();
    }

}
