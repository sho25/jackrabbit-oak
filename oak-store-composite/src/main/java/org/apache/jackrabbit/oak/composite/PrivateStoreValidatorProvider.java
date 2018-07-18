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
name|composite
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|*
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
name|CommitFailedException
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
name|commons
operator|.
name|PathUtils
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
name|commons
operator|.
name|PropertiesUtil
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
name|commit
operator|.
name|*
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
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|ServiceRegistration
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * {@link Validator} which detects change commits to the read only mounts.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|label
operator|=
literal|"Apache Jackrabbit Oak PrivateStoreValidatorProvider"
argument_list|)
specifier|public
class|class
name|PrivateStoreValidatorProvider
extends|extends
name|ValidatorProvider
block|{
specifier|private
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_PATH
init|=
literal|"/"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Fail when detecting commits to the read-only stores"
argument_list|,
name|description
operator|=
literal|"Commits will fail if set to true when detecting changes to any read-only store. If set to false the commit information is only logged."
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_FAIL_ON_DETECTION
init|=
literal|"failOnDetection"
decl_stmt|;
specifier|private
name|boolean
name|failOnDetection
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|MountInfoProvider
name|mountInfoProvider
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
specifier|private
name|ServiceRegistration
name|serviceRegistration
decl_stmt|;
annotation|@
name|NotNull
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
operator|new
name|PrivateStoreValidator
argument_list|(
name|ROOT_PATH
argument_list|)
return|;
block|}
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|failOnDetection
operator|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_FAIL_ON_DETECTION
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|mountInfoProvider
operator|.
name|hasNonDefaultMounts
argument_list|()
condition|)
block|{
name|serviceRegistration
operator|=
name|bundleContext
operator|.
name|registerService
argument_list|(
name|EditorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Enabling PrivateStoreValidatorProvider with failOnDetection {}"
argument_list|,
name|failOnDetection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|serviceRegistration
operator|!=
literal|null
condition|)
block|{
name|serviceRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|serviceRegistration
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|//For test purpose
name|void
name|setMountInfoProvider
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
block|}
comment|//For test purpose
name|void
name|setFailOnDetection
parameter_list|(
name|boolean
name|failOnDetection
parameter_list|)
block|{
name|this
operator|.
name|failOnDetection
operator|=
name|failOnDetection
expr_stmt|;
block|}
name|boolean
name|isFailOnDetection
parameter_list|()
block|{
return|return
name|failOnDetection
return|;
block|}
specifier|private
class|class
name|PrivateStoreValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|public
name|PrivateStoreValidator
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|checkPrivateStoreCommit
argument_list|(
name|getCommitPath
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|checkPrivateStoreCommit
argument_list|(
name|getCommitPath
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|checkPrivateStoreCommit
argument_list|(
name|getCommitPath
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Validator
name|checkPrivateStoreCommit
parameter_list|(
name|String
name|commitPath
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Mount
name|mountInfo
init|=
name|mountInfoProvider
operator|.
name|getMountByPath
argument_list|(
name|commitPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|mountInfo
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|Throwable
name|throwable
init|=
operator|new
name|Throwable
argument_list|(
literal|"Commit path: "
operator|+
name|commitPath
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Detected commit to a read-only store! "
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
if|if
condition|(
name|failOnDetection
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|UNSUPPORTED
argument_list|,
literal|0
argument_list|,
literal|"Unsupported commit to a read-only store!"
argument_list|,
name|throwable
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|PrivateStoreValidator
argument_list|(
name|commitPath
argument_list|)
return|;
block|}
specifier|private
name|String
name|getCommitPath
parameter_list|(
name|String
name|changeNodeName
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|changeNodeName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

