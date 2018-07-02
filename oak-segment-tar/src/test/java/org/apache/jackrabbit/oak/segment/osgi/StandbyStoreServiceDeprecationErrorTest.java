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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|StandbyStoreServiceDeprecationErrorTest
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
literal|"/OSGI-INF/org.apache.jackrabbit.oak.segment.osgi.StandbyStoreServiceDeprecationError.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasName
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.osgi.StandbyStoreServiceDeprecationError"
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
name|hasConfigurationPid
argument_list|(
literal|"org.apache.jackrabbit.oak.plugins.segment.standby.store.StandbyStoreService"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cd
operator|.
name|hasImplementationClass
argument_list|(
literal|"org.apache.jackrabbit.oak.segment.osgi.StandbyStoreServiceDeprecationError"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

