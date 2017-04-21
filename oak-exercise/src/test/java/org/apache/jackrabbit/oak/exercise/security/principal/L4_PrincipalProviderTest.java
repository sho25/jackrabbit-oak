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
name|exercise
operator|.
name|security
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalIterator
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|AbstractSecurityTest
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
name|exercise
operator|.
name|security
operator|.
name|principal
operator|.
name|CustomPrincipalConfiguration
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
name|exercise
operator|.
name|security
operator|.
name|principal
operator|.
name|CustomPrincipalProvider
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
name|namepath
operator|.
name|NamePathMapper
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
name|security
operator|.
name|principal
operator|.
name|PrincipalConfiguration
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
name|security
operator|.
name|principal
operator|.
name|PrincipalProvider
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
name|test
operator|.
name|NotExecutableException
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

begin_comment
comment|/**  *<pre>  * Module: Principal Management  * =============================================================================  *  * Title: Principal Provider  * -----------------------------------------------------------------------------  *  * Goal:  * Get familiar with the {@link org.apache.jackrabbit.oak.spi.security.principal.PrincipalProvider} interface.  *  * Exercises:  *  * - {@link #testCorrespondance()}  *   List the corresponding calls between {@link PrincipalManager} and {@link PrincipalProvider}.  *   List also those methods that have no correspondance in either interface.  *   Try to identify the reason for having a JCR-level manager and an Oak-level provider interface.  *  *  * Additional Exercises  * -----------------------------------------------------------------------------  *  * - Take a closer look at the {@link org.apache.jackrabbit.oak.spi.security.principal.PrincipalConfiguration}  *   and the available implementations.  *  *   Question: Can you identify how they are used and how principal management can  *             be extended both in an OSGi-based and regular java setup?  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * - Complete the {@link CustomPrincipalProvider}  *   stub and deploy the exercise bundle in a Sling base repository installation  *   (e.g. Cq|Granite).  *> Try to identify the tools that allow you to explore your custom principals  *> Play with the dynamic group membership as you define it in the principal provider and verify that the subjects calculated upon login are correct  *> Play with the authorization part of the principal management granting/revoking access for one of your custom principals  *</pre>  *  * @see org.apache.jackrabbit.oak.spi.security.principal.PrincipalProvider  * @see CustomPrincipalProvider  * @see CustomPrincipalConfiguration  */
end_comment

begin_class
specifier|public
class|class
name|L4_PrincipalProviderTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
name|String
name|testPrincipalName
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|principalProvider
operator|=
name|getConfig
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|principalManager
operator|=
name|getConfig
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|// NOTE: this method call doesn't make to much sense outside of a
comment|// simple test with a very limited number of principals (!!)
name|PrincipalIterator
name|principalIterator
init|=
name|principalManager
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|testPrincipalName
operator|=
name|principalIterator
operator|.
name|nextPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|testPrincipalName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrespondance
parameter_list|()
block|{
name|boolean
name|exists
init|=
name|principalManager
operator|.
name|hasPrincipal
argument_list|(
name|testPrincipalName
argument_list|)
decl_stmt|;
name|Principal
name|principal
init|=
name|principalManager
operator|.
name|getPrincipal
argument_list|(
name|testPrincipalName
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|principalIterator
init|=
name|principalManager
operator|.
name|findPrincipals
argument_list|(
name|testPrincipalName
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|groups
init|=
name|principalManager
operator|.
name|getGroupMembership
argument_list|(
name|principal
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|all
init|=
name|principalManager
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
comment|// EXERCISE: write the corresponding calls for the principal provider and verify the expected result
comment|// EXERCISE: which methods have nor corresponding call in the other interface?
block|}
block|}
end_class

end_unit
