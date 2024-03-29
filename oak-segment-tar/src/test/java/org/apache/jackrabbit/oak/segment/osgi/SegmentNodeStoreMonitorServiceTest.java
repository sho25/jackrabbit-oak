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
name|SegmentNodeStoreMonitorServiceTest
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
literal|"/OSGI-INF/org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasName
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService"
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
name|hasImplementationClass
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasReference
argument_list|(
literal|"snsStatsMBean"
argument_list|)
operator|.
name|withInterface
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreStatsMBean"
argument_list|)
operator|.
name|withMandatoryUnaryCardinality
argument_list|()
operator|.
name|withStaticPolicy
argument_list|()
operator|.
name|withField
argument_list|(
literal|"snsStatsMBean"
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
literal|"/OSGI-INF/metatype/org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService$Configuration.xml"
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
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService"
argument_list|)
operator|.
name|withReference
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService$Configuration"
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
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreMonitorService$Configuration"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ocd
operator|.
name|hasAttributeDefinition
argument_list|(
literal|"commitsTrackerWriterGroups"
argument_list|)
operator|.
name|withStringType
argument_list|()
operator|.
name|withCardinality
argument_list|(
literal|"2147483647"
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

