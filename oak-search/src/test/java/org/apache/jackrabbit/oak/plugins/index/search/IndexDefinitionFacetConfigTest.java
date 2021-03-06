begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|junit
operator|.
name|TemporarySystemProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexDefinition
operator|.
name|SecureFacetConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexDefinition
operator|.
name|SecureFacetConfiguration
operator|.
name|MODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|IndexDefinitionFacetConfigTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporarySystemProperty
name|temporarySystemProperty
init|=
operator|new
name|TemporarySystemProperty
argument_list|()
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|RANDOM_SEED
init|=
literal|1L
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|defaultConfig
parameter_list|()
block|{
name|SecureFacetConfiguration
name|config
init|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|config
operator|.
name|getMode
argument_list|()
argument_list|,
name|MODE
operator|.
name|SECURE
argument_list|)
expr_stmt|;
name|int
name|sampleSize
init|=
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonSecureConfigMode
parameter_list|()
block|{
name|SecureFacetConfiguration
name|config
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|"insecure"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|"statistical"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
argument_list|,
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|systemPropSecureFacet
parameter_list|()
block|{
name|SecureFacetConfiguration
name|config
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
literal|"random"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
literal|"secure"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
literal|"insecure"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|INSECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
literal|"insecure"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|"secure"
argument_list|)
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|systemPropSecureFacetStatisticalSampleSize
parameter_list|()
block|{
name|int
name|sampleSize
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
name|PROP_SECURE_FACETS_VALUE_STATISTICAL
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"-10"
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"100000000000"
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|root
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidSecureFacetSampleSize
parameter_list|()
block|{
name|int
name|sampleSize
decl_stmt|;
name|NodeBuilder
name|configBuilder
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"config"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
name|PROP_SECURE_FACETS_VALUE_STATISTICAL
argument_list|)
decl_stmt|;
name|NodeState
name|nodeState
init|=
name|configBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|configBuilder
operator|.
name|setProperty
argument_list|(
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
argument_list|,
operator|-
literal|10
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setProperty
argument_list|(
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
argument_list|,
operator|-
literal|20
argument_list|)
expr_stmt|;
name|nodeState
operator|=
name|configBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"-10"
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setProperty
argument_list|(
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
argument_list|,
operator|-
literal|20
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"-10"
argument_list|)
expr_stmt|;
name|configBuilder
operator|.
name|setProperty
argument_list|(
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|nodeState
operator|=
name|configBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|sampleSize
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderingOfOverrides
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
argument_list|,
literal|"insecure"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|NodeState
name|nodeState
decl_stmt|;
name|SecureFacetConfiguration
name|config
decl_stmt|;
name|int
name|sampleSize
decl_stmt|;
name|nodeState
operator|=
name|builder
operator|.
name|child
argument_list|(
literal|"config1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|"secure"
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
argument_list|)
expr_stmt|;
name|nodeState
operator|=
name|builder
operator|.
name|child
argument_list|(
literal|"config2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
name|PROP_SECURE_FACETS_VALUE_STATISTICAL
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
argument_list|,
literal|20
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
name|nodeState
operator|=
name|builder
operator|.
name|child
argument_list|(
literal|"config3"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
name|PROP_SECURE_FACETS_VALUE_STATISTICAL
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
name|sampleSize
operator|=
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|legacyConfig
parameter_list|()
block|{
name|NodeState
name|ns
init|=
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|true
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|SecureFacetConfiguration
name|config
init|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|=
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_SECURE_FACETS
argument_list|,
literal|false
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|config
operator|=
name|SecureFacetConfiguration
operator|.
name|getInstance
argument_list|(
name|RANDOM_SEED
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|INSECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|config
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|absentFacetConfigNode
parameter_list|()
block|{
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|root
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|SecureFacetConfiguration
name|config
init|=
name|idxDefn
operator|.
name|getSecureFacetConfiguration
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MODE
operator|.
name|SECURE
argument_list|,
name|config
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|randomSeed
parameter_list|()
block|{
name|long
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_RANDOM_SEED
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|root
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|root
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|SecureFacetConfiguration
name|config
init|=
name|idxDefn
operator|.
name|getSecureFacetConfiguration
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|seed
argument_list|,
name|config
operator|.
name|getRandomSeed
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|randomSeedWithoutOneInDef
parameter_list|()
block|{
name|long
name|seed1
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|root
argument_list|,
literal|"/foo"
argument_list|)
operator|.
name|getSecureFacetConfiguration
argument_list|()
operator|.
name|getRandomSeed
argument_list|()
decl_stmt|;
name|long
name|seed2
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|root
argument_list|,
literal|"/foo"
argument_list|)
operator|.
name|getSecureFacetConfiguration
argument_list|()
operator|.
name|getRandomSeed
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|seed1
argument_list|,
name|seed2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

