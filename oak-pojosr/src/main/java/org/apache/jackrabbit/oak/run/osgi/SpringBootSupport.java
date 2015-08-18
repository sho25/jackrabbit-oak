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
name|run
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|JarURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarFile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|AbstractIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|connect
operator|.
name|Revision
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|connect
operator|.
name|launch
operator|.
name|BundleDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * For running PojoSR based application in Spring Boot environment we need to provide  * a custom Revision implementation. Default PojoSR JarRevision support works for normal  * jar files while in Spring Boot env jar files are embedded withing another jar.  *  * This class does not have direct dependency on Spring Boot Jar Launcher but relies  * on reflection as the Spring Jar support is not visible to PojoSR classloader  */
end_comment

begin_class
class|class
name|SpringBootSupport
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SpringBootSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SPRING_BOOT_PACKAGE
init|=
literal|"org.springframework.boot.loader.jar"
decl_stmt|;
specifier|public
specifier|static
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|processDescriptors
parameter_list|(
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|descriptors
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BundleDescriptor
argument_list|>
name|processed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|BundleDescriptor
name|desc
range|:
name|descriptors
control|)
block|{
if|if
condition|(
name|desc
operator|.
name|getRevision
argument_list|()
operator|==
literal|null
condition|)
block|{
name|URL
name|u
init|=
operator|new
name|URL
argument_list|(
name|desc
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|URLConnection
name|uc
init|=
name|u
operator|.
name|openConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|uc
operator|instanceof
name|JarURLConnection
operator|&&
name|uc
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|SPRING_BOOT_PACKAGE
argument_list|)
condition|)
block|{
name|Revision
name|rev
init|=
operator|new
name|SpringBootJarRevision
argument_list|(
operator|(
operator|(
name|JarURLConnection
operator|)
name|uc
operator|)
operator|.
name|getJarFile
argument_list|()
argument_list|,
name|uc
operator|.
name|getLastModified
argument_list|()
argument_list|)
decl_stmt|;
name|desc
operator|=
operator|new
name|BundleDescriptor
argument_list|(
name|desc
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|desc
operator|.
name|getUrl
argument_list|()
argument_list|,
name|desc
operator|.
name|getHeaders
argument_list|()
argument_list|,
name|rev
argument_list|,
name|desc
operator|.
name|getServices
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|processed
operator|.
name|add
argument_list|(
name|desc
argument_list|)
expr_stmt|;
block|}
return|return
name|processed
return|;
block|}
comment|/**      * Key change here is use of org.springframework.boot.loader.jar.JarEntry.getUrl()      * to get a working URL which allows access to files present in embedded jars      */
specifier|private
specifier|static
class|class
name|SpringBootJarRevision
implements|implements
name|Revision
block|{
specifier|private
specifier|static
name|Method
name|ENTRY_URL_METHOD
decl_stmt|;
specifier|private
specifier|final
name|JarFile
name|jarFile
decl_stmt|;
specifier|private
specifier|final
name|long
name|lastModified
decl_stmt|;
specifier|private
name|SpringBootJarRevision
parameter_list|(
name|JarFile
name|jarFile
parameter_list|,
name|long
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|jarFile
operator|=
name|jarFile
expr_stmt|;
comment|//Taken from org.apache.felix.connect.JarRevision
if|if
condition|(
name|lastModified
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|lastModified
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
annotation|@
name|Override
specifier|public
name|URL
name|getEntry
parameter_list|(
name|String
name|entryName
parameter_list|)
block|{
try|try
block|{
name|JarEntry
name|jarEntry
init|=
name|jarFile
operator|.
name|getJarEntry
argument_list|(
name|entryName
argument_list|)
decl_stmt|;
comment|/*                   JarEntry here is instance of org.springframework.boot.loader.jar.JarEntry                   However as that class is loaded in different classloader and not visible                   actual invocation has to be done via reflection. URL returned here has proper                   Handler configured to allow reverse access via URL connection                  */
return|return
operator|(
name|URL
operator|)
name|getUrlMethod
argument_list|(
name|jarEntry
argument_list|)
operator|.
name|invoke
argument_list|(
name|jarEntry
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while fetching jar entry {} from {}"
argument_list|,
name|entryName
argument_list|,
name|jarFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getEntries
parameter_list|()
block|{
specifier|final
name|Enumeration
argument_list|<
name|JarEntry
argument_list|>
name|e
init|=
name|jarFile
operator|.
name|entries
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
operator|new
name|AbstractIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
return|return
name|e
operator|.
name|nextElement
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Method
name|getUrlMethod
parameter_list|(
name|JarEntry
name|jarEntry
parameter_list|)
throws|throws
name|NoSuchMethodException
block|{
if|if
condition|(
name|ENTRY_URL_METHOD
operator|==
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|jarEntry
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|SPRING_BOOT_PACKAGE
argument_list|)
argument_list|,
literal|"JarEntry class %s does not belong to Spring package"
argument_list|,
name|jarEntry
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|ENTRY_URL_METHOD
operator|=
name|jarEntry
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getUrl"
argument_list|)
expr_stmt|;
block|}
return|return
name|ENTRY_URL_METHOD
return|;
block|}
block|}
block|}
end_class

end_unit

