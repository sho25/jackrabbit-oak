begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
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
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|Maps
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
name|authorization
operator|.
name|PrivilegeManager
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|Root
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
name|jcr
operator|.
name|delegate
operator|.
name|SessionDelegate
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
name|LocalNameMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|plugins
operator|.
name|name
operator|.
name|Namespaces
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
name|plugins
operator|.
name|nodetype
operator|.
name|DefinitionProvider
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
name|plugins
operator|.
name|nodetype
operator|.
name|EffectiveNodeTypeProvider
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
name|plugins
operator|.
name|observation
operator|.
name|ObservationManagerImpl
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
name|plugins
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|SecurityConfiguration
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
name|SecurityProvider
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
name|authorization
operator|.
name|PermissionProvider
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * TODO doc  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SessionContext
implements|implements
name|NamePathMapper
block|{
specifier|private
specifier|final
name|RepositoryImpl
name|repository
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|AccessControlManager
name|accessControlManager
decl_stmt|;
specifier|private
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
specifier|private
name|ObservationManagerImpl
name|observationManager
decl_stmt|;
specifier|private
name|SessionContext
parameter_list|(
name|RepositoryImpl
name|repository
parameter_list|,
name|SessionDelegate
name|delegate
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ValueFactory
name|valueFactory
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|valueFactory
expr_stmt|;
block|}
specifier|public
specifier|static
name|SessionContext
name|create
parameter_list|(
specifier|final
name|SessionDelegate
name|delegate
parameter_list|,
name|RepositoryImpl
name|repository
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Root
name|root
init|=
name|checkNotNull
argument_list|(
name|delegate
argument_list|)
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|LocalNameMapper
name|nameMapper
init|=
operator|new
name|LocalNameMapper
argument_list|(
name|namespaces
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|()
block|{
return|return
name|Namespaces
operator|.
name|getNamespaceMap
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|NamePathMapperImpl
name|namePathMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|nameMapper
argument_list|,
name|delegate
operator|.
name|getIdManager
argument_list|()
argument_list|)
decl_stmt|;
name|ValueFactoryImpl
name|valueFactory
init|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
operator|.
name|getBlobFactory
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|SessionContext
argument_list|(
name|checkNotNull
argument_list|(
name|repository
argument_list|)
argument_list|,
name|delegate
argument_list|,
name|namePathMapper
argument_list|,
name|valueFactory
argument_list|)
block|{
specifier|private
specifier|final
name|SessionImpl
name|session
init|=
operator|new
name|SessionImpl
argument_list|(
name|this
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|WorkspaceImpl
name|workspace
init|=
operator|new
name|WorkspaceImpl
argument_list|(
name|this
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
annotation|@
name|Override
specifier|public
name|WorkspaceImpl
name|getWorkspaceInternal
parameter_list|()
block|{
return|return
name|workspace
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
specifier|public
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
specifier|public
specifier|abstract
name|Session
name|getSession
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|WorkspaceImpl
name|getWorkspaceInternal
parameter_list|()
function_decl|;
specifier|public
name|Workspace
name|getWorkspace
parameter_list|()
block|{
return|return
name|getWorkspaceInternal
argument_list|()
return|;
block|}
specifier|public
name|LockManager
name|getLockManager
parameter_list|()
block|{
return|return
name|getWorkspaceInternal
argument_list|()
operator|.
name|getLockManager
argument_list|()
return|;
block|}
specifier|public
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|getWorkspaceInternal
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
return|;
block|}
specifier|public
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getWorkspaceInternal
argument_list|()
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
specifier|public
name|EffectiveNodeTypeProvider
name|getEffectiveNodeTypeProvider
parameter_list|()
block|{
return|return
name|getWorkspaceInternal
argument_list|()
operator|.
name|getReadWriteNodeTypeManager
argument_list|()
return|;
block|}
specifier|public
name|DefinitionProvider
name|getDefinitionProvider
parameter_list|()
block|{
return|return
name|getWorkspaceInternal
argument_list|()
operator|.
name|getReadWriteNodeTypeManager
argument_list|()
return|;
block|}
specifier|public
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|valueFactory
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|accessControlManager
operator|==
literal|null
condition|)
block|{
comment|// TODO
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|delegate
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|getPermissionProvider
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|accessControlManager
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|AccessControlManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|run
parameter_list|()
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
return|return
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getAccessControlManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|accessControlManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|permissionProvider
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|permissionProvider
operator|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getPermissionProvider
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|delegate
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|permissionProvider
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
return|return
name|permissionProvider
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
block|{
if|if
condition|(
name|principalManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|principalManager
operator|=
name|securityProvider
operator|.
name|getPrincipalConfiguration
argument_list|()
operator|.
name|getPrincipalManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|principalManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|UserManager
name|getUserManager
parameter_list|()
block|{
if|if
condition|(
name|userManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|userManager
operator|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|userManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
block|{
if|if
condition|(
name|privilegeManager
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|repository
operator|.
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|privilegeManager
operator|=
name|securityProvider
operator|.
name|getPrivilegeConfiguration
argument_list|()
operator|.
name|getPrivilegeManager
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|privilegeManager
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
comment|// TODO: take non-security related importers into account as well (proper configuration)
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|importers
init|=
operator|new
name|ArrayList
argument_list|<
name|ProtectedItemImporter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|repository
operator|.
name|getSecurityProvider
argument_list|()
operator|.
name|getSecurityConfigurations
argument_list|()
control|)
block|{
name|importers
operator|.
name|addAll
argument_list|(
name|sc
operator|.
name|getProtectedItemImporters
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|importers
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|ObservationManager
name|getObservationManager
parameter_list|()
block|{
if|if
condition|(
name|observationManager
operator|==
literal|null
condition|)
block|{
name|observationManager
operator|=
operator|new
name|ObservationManagerImpl
argument_list|(
name|delegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|namePathMapper
argument_list|,
name|repository
operator|.
name|getObservationExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|observationManager
return|;
block|}
specifier|public
name|boolean
name|hasPendingEvents
parameter_list|()
block|{
return|return
name|observationManager
operator|!=
literal|null
operator|&&
name|observationManager
operator|.
name|hasEvents
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< NamePathMapper>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakNameOrNull
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
name|namePathMapper
operator|.
name|hasSessionLocalMappings
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJcrName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getOakPathKeepIndex
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link javax.jcr.RepositoryException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws javax.jcr.RepositoryException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrow
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid name or path: "
operator|+
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the Oak path for the given JCR path, or throws a      * {@link javax.jcr.PathNotFoundException} if the path can not be mapped.      *      * @param jcrPath JCR path      * @return Oak path      * @throws javax.jcr.PathNotFoundException if the path can not be mapped      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|/**      * Shortcut for {@code SessionDelegate.getOakPathKeepIndex(jcrPath)}.      *      * @param jcrPath JCR path      * @return Oak path, or {@code null}, with indexes left intact      * @throws javax.jcr.PathNotFoundException      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getOakPathKeepIndexOrThrowNotFound
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakPath
init|=
name|namePathMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
return|return
name|oakPath
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|jcrPath
argument_list|)
throw|;
block|}
block|}
comment|//------------------------------------------------------------< internal>---
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|observationManager
operator|!=
literal|null
condition|)
block|{
name|observationManager
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

