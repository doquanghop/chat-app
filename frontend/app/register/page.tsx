import RegisterForm from "@/components/register-form"

export default function Register() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
      <div className="w-full max-w-md p-8 space-y-8 bg-white rounded-lg shadow">
        <div className="text-center">
          <h1 className="text-2xl font-bold">Create an Account</h1>
          <p className="mt-2 text-gray-600">Sign up to start messaging</p>
        </div>
        <RegisterForm />
        <div className="text-center text-sm">
          <p>
            Already have an account?{" "}
            <a href="/" className="font-medium text-blue-600 hover:text-blue-500">
              Sign in
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}
