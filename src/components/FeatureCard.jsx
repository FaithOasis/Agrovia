export default function FeatureCard({ title, description, icon }) {
  return (
    <div className="bg-white p-6 shadow rounded-lg text-center">
      <div className="text-4xl mb-4">{icon}</div>
      <h3 className="text-xl font-semibold text-green-600">{title}</h3>
      <p className="mt-2 text-gray-600">{description}</p>
    </div>
  );
}
