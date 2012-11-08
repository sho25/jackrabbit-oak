begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|testing
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|util
operator|.
name|Chronometer
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
name|mk
operator|.
name|util
operator|.
name|Configuration
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
name|mk
operator|.
name|util
operator|.
name|MicroKernelConfigProvider
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
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * The test base class for tests that are using only one microkernel instance.  *  *  *  */
end_comment

begin_class
specifier|public
class|class
name|MicroKernelTestBase
block|{
specifier|static
name|MicroKernelInitializer
name|initializator
decl_stmt|;
specifier|public
name|MicroKernel
name|mk
decl_stmt|;
specifier|public
specifier|static
name|Configuration
name|conf
decl_stmt|;
specifier|public
name|Chronometer
name|chronometer
decl_stmt|;
comment|/**      * Loads the corresponding microkernel initialization class and the      * microkernel configuration.The method searches for the<b>mk.type</b>      * system property in order to initialize the proper microkernel.By default,      * the oak microkernel will be instantiated.      *      * @throws Exception      */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
comment|// FIXME - Add back
name|initializator
operator|=
operator|new
name|OakMicroKernelInitializer
argument_list|()
expr_stmt|;
comment|//String mktype = System.getProperty("mk.type");
comment|//initializator = (mktype == null || mktype.equals("oakmk")) ? new OakMicroKernelInitializer()
comment|//        : new MongoMicroKernelInitializer();
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Tests will run against ***"
operator|+
name|initializator
operator|.
name|getType
argument_list|()
operator|+
literal|"***"
argument_list|)
expr_stmt|;
name|conf
operator|=
name|MicroKernelConfigProvider
operator|.
name|readConfig
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a microkernel collection with only one microkernel.      *      * @throws Exception      */
annotation|@
name|Before
specifier|public
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|=
operator|(
operator|new
name|MicroKernelCollection
argument_list|(
name|initializator
argument_list|,
name|conf
argument_list|,
literal|1
argument_list|)
operator|)
operator|.
name|getMicroKernels
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|chronometer
operator|=
operator|new
name|Chronometer
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

