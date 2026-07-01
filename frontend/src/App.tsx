import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './auth/AuthProvider'
import { AuthGuard } from './auth/AuthGuard'
import { AppLayout } from './layout/AppLayout'
import { ResourceListPage } from './resources/ResourceListPage'

function CallbackPage() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
      Signing you in...
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/callback" element={<CallbackPage />} />
          <Route
            path="/"
            element={
              <AuthGuard>
                <AppLayout />
              </AuthGuard>
            }
          >
            <Route index element={<ResourceListPage resourcePath="/v1/ingredients" title="Ingredients" />} />
            <Route path="ingredients" element={<ResourceListPage resourcePath="/v1/ingredients" title="Ingredients" />} />
            <Route path="nutrients" element={<ResourceListPage resourcePath="/v1/nutrients" title="Nutrients" />} />
            <Route path="recipes" element={<ResourceListPage resourcePath="/v1/recipes" title="Recipes" />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
