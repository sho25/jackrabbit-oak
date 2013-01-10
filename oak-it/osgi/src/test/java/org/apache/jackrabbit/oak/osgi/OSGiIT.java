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
name|oak
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|oak
operator|.
name|api
operator|.
name|ContentRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|JUnit4TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|options
operator|.
name|SystemPropertyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|options
operator|.
name|UrlProvisionOption
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|bundle
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|junitBundles
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|mavenBundle
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|systemProperties
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|JUnit4TestRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|OSGiIT
block|{
annotation|@
name|Configuration
specifier|public
name|Option
index|[]
name|configuration
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
name|CoreOptions
operator|.
name|options
argument_list|(
name|junitBundles
argument_list|()
argument_list|,
name|mavenBundle
argument_list|(
literal|"org.apache.felix"
argument_list|,
literal|"org.apache.felix.scr"
argument_list|,
literal|"1.6.0"
argument_list|)
argument_list|,
name|mavenBundle
argument_list|(
literal|"org.apache.felix"
argument_list|,
literal|"org.apache.felix.configadmin"
argument_list|,
literal|"1.4.0"
argument_list|)
argument_list|,
name|mavenBundle
argument_list|(
literal|"org.apache.felix"
argument_list|,
literal|"org.apache.felix.fileinstall"
argument_list|,
literal|"3.2.6"
argument_list|)
argument_list|,
name|systemProperties
argument_list|(
operator|new
name|SystemPropertyOption
argument_list|(
literal|"felix.fileinstall.dir"
argument_list|)
operator|.
name|value
argument_list|(
name|getConfigDir
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"jcr.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"guava.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"jackrabbit-api.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"jackrabbit-jcr-commons.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-commons.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-mk-api.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-mk.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-mk-remote.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-core.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-lucene.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"oak-jcr.jar"
argument_list|)
argument_list|,
name|jarBundle
argument_list|(
literal|"tika-core.jar"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|getConfigDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
literal|"src"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|"config"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|private
name|UrlProvisionOption
name|jarBundle
parameter_list|(
name|String
name|jar
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|File
name|target
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|File
name|bundles
init|=
operator|new
name|File
argument_list|(
name|target
argument_list|,
literal|"test-bundles"
argument_list|)
decl_stmt|;
return|return
name|bundle
argument_list|(
operator|new
name|File
argument_list|(
name|bundles
argument_list|,
name|jar
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Inject
specifier|private
name|MicroKernel
name|kernel
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-454"
argument_list|)
specifier|public
name|void
name|testMicroKernel
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|kernel
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Pattern
operator|.
name|matches
argument_list|(
literal|"[0-9a-f]+"
argument_list|,
name|kernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
specifier|private
name|ContentRepository
name|oakRepository
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testOakRepository
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|oakRepository
argument_list|)
expr_stmt|;
comment|// TODO: try something with oakRepository
block|}
annotation|@
name|Inject
specifier|private
name|Repository
name|jcrRepository
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testJcrRepository
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|jcrRepository
argument_list|)
expr_stmt|;
comment|// TODO: try something with jcrRepository
block|}
block|}
end_class

end_unit

