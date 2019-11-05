begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|SyncHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|SyncManagerImplTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
name|SyncManagerImpl
name|syncManager
init|=
operator|new
name|SyncManagerImpl
argument_list|()
decl_stmt|;
annotation|@
name|Before
argument_list|()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|syncManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHandler
parameter_list|()
block|{
name|assertNull
argument_list|(
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
name|SyncHandler
name|syncHandler
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|SyncHandler
operator|.
name|class
argument_list|,
name|syncHandler
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|syncHandler
argument_list|,
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
