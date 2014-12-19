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
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|getenv
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|NodeStore
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
name|PaxExam
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
name|DefaultCompositeOption
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
name|spi
operator|.
name|reactors
operator|.
name|ExamReactorStrategy
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
name|spi
operator|.
name|reactors
operator|.
name|PerClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Bundle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|InvalidSyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|PaxExam
operator|.
name|class
argument_list|)
annotation|@
name|ExamReactorStrategy
argument_list|(
name|PerClass
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
literal|"1.8.0"
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
name|mavenBundle
argument_list|(
literal|"org.ops4j.pax.logging"
argument_list|,
literal|"pax-logging-api"
argument_list|,
literal|"1.7.2"
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
name|jarBundles
argument_list|()
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
name|Option
name|jarBundles
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|DefaultCompositeOption
name|composite
init|=
operator|new
name|DefaultCompositeOption
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|bundle
range|:
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"test-bundles"
argument_list|)
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|bundle
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|&&
name|bundle
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|composite
operator|.
name|add
argument_list|(
name|bundle
argument_list|(
name|bundle
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
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|composite
return|;
block|}
annotation|@
name|Inject
specifier|private
name|BundleContext
name|context
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|listBundles
parameter_list|()
block|{
name|assumeTrue
argument_list|(
operator|!
name|buildBotTrunkLinux
argument_list|()
argument_list|)
expr_stmt|;
comment|// FIXME OAK-2374: fails often on http://ci.apache.org/builders/oak-trunk.
for|for
control|(
name|Bundle
name|bundle
range|:
name|context
operator|.
name|getBundles
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|bundle
argument_list|)
expr_stmt|;
block|}
block|}
comment|// FIXME this is a copy of CIHelper.buildBotTrunkLinux() due to OSGi/Maven shortcomings. See OAK-2374
specifier|private
specifier|static
name|boolean
name|buildBotTrunkLinux
parameter_list|()
block|{
name|String
name|user
init|=
name|getenv
argument_list|(
literal|"USER"
argument_list|)
decl_stmt|;
return|return
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|startsWith
argument_list|(
literal|"buildslave3"
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|listServices
parameter_list|()
throws|throws
name|InvalidSyntaxException
block|{
for|for
control|(
name|ServiceReference
name|reference
range|:
name|context
operator|.
name|getAllServiceReferences
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Inject
specifier|private
name|NodeStore
name|store
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNodeStore
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
specifier|private
name|Repository
name|repository
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testRepository
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|repository
operator|.
name|getDescriptor
argument_list|(
name|Repository
operator|.
name|REP_NAME_DESC
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

