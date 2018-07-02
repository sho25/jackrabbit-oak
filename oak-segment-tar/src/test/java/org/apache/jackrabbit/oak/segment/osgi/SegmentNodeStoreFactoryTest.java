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
name|segment
operator|.
name|osgi
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|segment
operator|.
name|osgi
operator|.
name|MetatypeInformation
operator|.
name|ObjectClassDefinition
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

begin_class
specifier|public
class|class
name|SegmentNodeStoreFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testComponentDescriptor
parameter_list|()
throws|throws
name|Exception
block|{
name|ComponentDescriptor
name|cd
init|=
name|ComponentDescriptor
operator|.
name|open
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/OSGI-INF/org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasName
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasRequireConfigurationPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasActivateMethod
argument_list|(
literal|"activate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasDeactivateMethod
argument_list|(
literal|"deactivate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasProperty
argument_list|(
literal|"role"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasProperty
argument_list|(
literal|"customBlobStore"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasProperty
argument_list|(
literal|"customSegmentStore"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasProperty
argument_list|(
literal|"registerDescriptors"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasReference
argument_list|(
literal|"blobStore"
argument_list|)
operator|.
name|withInterface
argument_list|(
literal|"org.apache.jackrabbit.oak.spi.blob.BlobStore"
argument_list|)
operator|.
name|withOptionalUnaryCardinality
argument_list|()
operator|.
name|withStaticPolicy
argument_list|()
operator|.
name|withGreedyPolicyOption
argument_list|()
operator|.
name|withTarget
argument_list|(
literal|"(&(!(split.blobstore=old))(!(split.blobstore=new)))"
argument_list|)
operator|.
name|withBind
argument_list|(
literal|"bindBlobStore"
argument_list|)
operator|.
name|withUnbind
argument_list|(
literal|"unbindBlobStore"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasReference
argument_list|(
literal|"segmentStore"
argument_list|)
operator|.
name|withInterface
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.spi.persistence.SegmentNodeStorePersistence"
argument_list|)
operator|.
name|withOptionalUnaryCardinality
argument_list|()
operator|.
name|withStaticPolicy
argument_list|()
operator|.
name|withGreedyPolicyOption
argument_list|()
operator|.
name|withBind
argument_list|(
literal|"bindSegmentStore"
argument_list|)
operator|.
name|withUnbind
argument_list|(
literal|"unbindSegmentStore"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasReference
argument_list|(
literal|"statisticsProvider"
argument_list|)
operator|.
name|withInterface
argument_list|(
literal|"org.apache.jackrabbit.oak.stats.StatisticsProvider"
argument_list|)
operator|.
name|withMandatoryUnaryCardinality
argument_list|()
operator|.
name|withStaticPolicy
argument_list|()
operator|.
name|withBind
argument_list|(
literal|"bindStatisticsProvider"
argument_list|)
operator|.
name|withUnbind
argument_list|(
literal|"unbindStatisticsProvider"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetatypeInformation
parameter_list|()
throws|throws
name|Exception
block|{
name|MetatypeInformation
name|mi
init|=
name|MetatypeInformation
operator|.
name|open
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/OSGI-INF/metatype/org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mi
operator|.
name|hasDesignate
argument_list|()
operator|.
name|withPid
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory"
argument_list|)
operator|.
name|withFactoryPid
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory"
argument_list|)
operator|.
name|withReference
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectClassDefinition
name|ocd
init|=
name|mi
operator|.
name|getObjectClassDefinition
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreFactory"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"role"
argument_list|)
operator|.
name|withStringType
argument_list|()
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"customBlobStore"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withDefaultValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"customBlobStore"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withDefaultValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"customSegmentStore"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withDefaultValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"registerDescriptors"
argument_list|)
operator|.
name|withBooleanType
argument_list|()
operator|.
name|withDefaultValue
argument_list|(
literal|"false"
argument_list|)
operator|.
name|check
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

